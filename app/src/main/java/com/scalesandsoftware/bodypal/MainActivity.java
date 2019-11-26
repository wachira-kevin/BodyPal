package com.scalesandsoftware.bodypal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //BLUETOOTH
    BluetoothAdapter bluetoothAdapter;
    BluetoothService mBluetoothService;
    BluetoothDevice mBtDevice;

    public ArrayList<BluetoothDevice> mBtDevices;

    //VIEWS
    Toolbar toolbar;
    TextView weightTextView;
    StringBuilder message;

    //DEVICE UUID
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static final String TAG = "MainActivity";

    //PERMISSION REQUEST CODE
    private static final int BLUETOOTH_REQUEST_CODE = 1;
    private static final int FINE_LOCATION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZING VIEWs
        weightTextView = findViewById(R.id.weightTextView);
        message = new StringBuilder();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBtDevices = new ArrayList<>();

        IntentFilter messageIntent = new IntentFilter("IncomingMessage");
        registerReceiver(messageReceiver, messageIntent);


    }

    //TOOLBAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        requestPermission();
        SubMenu deviceMenu = menu.getItem(4).getSubMenu();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                deviceMenu.add(0,10, Menu.NONE, deviceName);

            }
        }

        return true;
    }

    //ITEM CLICK LISTENER
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingsItem:
            case R.id.resultsItem:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.bluetoothItem:
                requestPermission();
                return true;
            case R.id.logOutItem:
                signOut();
                return true;
            case 10:
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //REQUESTING BLUETOOTH PERMISSION FROM USER
    private void requestPermission(){
        //BLUETOOTH ANDROID ADAPTER
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //CHECKING FOR DEVICE BLUETOOTH COMPATIBILITY
        if (bluetoothAdapter != null){
            if (!bluetoothAdapter.isEnabled()){
                //REQUEST FOR BLUETOOTH PERMISSION
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);

            }
        }else{
            Toast.makeText(this, "your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        }

    }

    //HANDLE REQUEST RESPONSE
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if ( requestCode == BLUETOOTH_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //TODO MEASURE WEIGHT METHOD
            }else{
                //PERMISSION WAS NOT GRANTED
                Toast.makeText(this, "permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //PERMISSIONS
    private void permissions(){
        //CHECK IF PERMISSION IS GRANTED
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            //REQUEST FOR PERMISSION
            if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION )){
                Toast.makeText(this, "Bluetooth Permission is needed to connect to scale and measure weight", Toast.LENGTH_SHORT).show();
            }else if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_COARSE_LOCATION)){
                Toast.makeText(this, "Bluetooth Permission is needed to connect to scale and measure weight", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},FINE_LOCATION_REQUEST_CODE);
            }
        }
    }

    //START BLUETOOTH SERVICE
    public void startBluetoothService(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBluetoothService: Initializing RFCOM bluetooth connection");
        mBluetoothService.startClient(device, uuid);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    //MESSAGE RECEIVER
    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String text = intent.getStringExtra("theWeight");
            message.append(text);
            weightTextView.setText(message);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);

    }
}
