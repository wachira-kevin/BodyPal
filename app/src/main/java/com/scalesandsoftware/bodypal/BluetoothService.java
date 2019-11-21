package com.scalesandsoftware.bodypal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final String appName = "BodyPal";

    //UNIVERSAL BLUETOOTH CONNECTION UUID
    private static final UUID MY_INSECURE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //VARIABLES
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private AcceptThread mInsecureAcceptThread;
    private UUID deviceUUID;
    private Context mContext;

    // CLASS CONSTRUCTOR
    BluetoothService (Context context){
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    //ACCEPT THREAD: SETS UP CONNECTION SOCKETS
    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mServerSocket;

        AcceptThread(){
            BluetoothServerSocket temp = null;

            try {
                //CREATE A NEW LISTENING SERVER SOCKET
                temp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_INSECURE_UUID);
                Log.d(TAG, "AcceptThread: setting up server using :" + MY_INSECURE_UUID);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: Could not set up server", e);
            }
            mServerSocket = temp;
        }

        //RUNNING THREAD
        public void run(){
            Log.d(TAG, "run: AcceptThread is Running");
            BluetoothSocket socket;
            while (true) {
                try {
                    //STARTING SOCKET
                    Log.d(TAG, "run: RFCOM Server socket start.......");
                    socket = mServerSocket.accept();
                    Log.d(TAG, "run: RFCOM Server socket accepted connection");
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    //HANDLING SOCKET CONNECTION
                    connected(socket,mBluetoothDevice);
                }
            }

        }

        //CANCELING THREAD
        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

    }

    //CONNECTION THREAD: INITIATES CONNECTION USING ACCEPT THREAD
    private class ConnectThread extends Thread{
        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        ConnectThread(BluetoothDevice device, UUID uuid) {
            BluetoothSocket temp = null;
            mDevice = device;
            deviceUUID = uuid;

            try{
                //GET THE BLUETOOTH SOCKET TO CONNECT WITH THE GIVEN BLUETOOTH DEVICE
                temp = device.createRfcommSocketToServiceRecord(deviceUUID);
            }catch(IOException e){
                Log.e(TAG, "ConnectThread: Socket's create() method failed", e);
            }
            mSocket = temp;
        }

        public void run(){
            //CANCEL DISCOVERY SINCE IT SLOWS DOWN THE CONNECTION
            mBluetoothAdapter.cancelDiscovery();
            try {
                mSocket.connect();
                Log.d(TAG, "run: Connection was Succesful");
            } catch (IOException connectException) {
                //UNABLE TO CONNECT: CLOSE SOCKET AND RETURN
                try{
                    Log.d(TAG, "run: Trying to close connection");
                    mSocket.close();
                }catch (IOException closeException){
                    Log.e(TAG, "run: could not close the client socket", closeException);
                }
                Log.d(TAG, "ConnectThread: could not connect to UUID " + MY_INSECURE_UUID);
            }

            connected(mSocket,mDevice);
        }

        //CANCELS CONNECT THREAD
        void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

    }

    //CONNECTED THREAD: HANDLES COMMUNICATION BETWEEN THE DEVICES
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;

        //INITIALIZING INPUT STREAM TO HANDLE INPUTS FROM THE SCALE
        private final InputStream mInStream;

        ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: connected thread starting");
            mSocket = socket;
            InputStream tempIn = null;

            try {
                //GETTING WEIGHT INPUTS
                tempIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            mInStream = tempIn;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            while(true){
                try{
                    //READING THE INPUTS
                    bytes = mInStream.read(buffer);
                    String IncomingMessage = new String(buffer, "US-ASCII");
                    Log.d(TAG, "InputStream  int bytes;: " + IncomingMessage);

                    //HANDLING INPUTS FOR DISPLAY
                    Intent incomingWeight = new Intent("IncomingMessage");
                    incomingWeight.putExtra("theWeight", IncomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingWeight);


                }catch (IOException e){
                    e.printStackTrace();
                    break;
                }
            }
        }

        void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    //INITIALIZING CONNECTION
    private void connected(BluetoothSocket mSocket, BluetoothDevice mDevice){
        Log.d(TAG, "connected: Connected to socket");

        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

    //STARTING CLIENT
    void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: success.....");
        mConnectThread = new ConnectThread(device,uuid);
        mConnectThread.start();

    }

    //HANDLING THREADS
    private synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }
}

