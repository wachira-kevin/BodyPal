package com.scalesandsoftware.bodypal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = "LOGIN ACTIVITY";

    //VARIABLES
    Button login, signUp;
    EditText email, password;

    //INSTANCE OF FIREBASE AUTHENTICATION
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //INITIALIZING VIEWS
        login = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUpButtom);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        //INITIALIZING FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
    }

    //LOGIN AUTHENTICATION
    public void btnLogin(View view) {
        //START ACTIVITY INTENT
        final Intent intent = new Intent(this, MainActivity.class);

        //CONVERTING EDIT TEXT TO STRINGS
        String userLoginEmail = email.getText().toString();
        String userLoginPassword = password.getText().toString();

        //FIREBASE SIGN IN USER BY EMAIL AND PASSWORD
        mAuth.signInWithEmailAndPassword(userLoginEmail, userLoginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onClick: trying to sign in with email and password");
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: signIn with email successful");
                    startActivity(intent);
                } else {
                    Log.w(TAG, "onComplete: signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "failed to sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO HANDLE WRONG INPUTS
    }

    //SIGN UP AUTHENTICATION
    public void btnSignUp(View view) {
        //START ACTIVITY INTENT
        //TODO LINK TO ACCOUNT SETTINGS
        final Intent intent = new Intent(this, MainActivity.class);

        //CONVERTING EDIT TEXT TO STRINGS
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        //FIREBASE CREATE USER BY EMAIL AND PASSWORD FUNCTION
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(intent);
                    Log.d(TAG, "onComplete: created user with email successfully");
                } else {
                    Log.w(TAG, "onComplete: create user with email failed ", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO HANDLE WRONG INPUTS
    }
}
