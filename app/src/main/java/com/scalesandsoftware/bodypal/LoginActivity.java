package com.scalesandsoftware.bodypal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.text.TextUtils;
import android.content.Intent;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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
        signUp = findViewById(R.id.signUpButton);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        //INITIALIZING FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
    }

    //LOGIN AUTHENTICATION
    public void btnLogin(View view) {
        //START ACTIVITY INTENT
        final Intent mainActivityIntent = new Intent(this, MainActivity.class);
        final Intent verificationIntent = new Intent(this, VerificationActivity.class);

        //CONVERTING EDIT TEXT TO STRINGS
        String userLoginEmail = email.getText().toString();
        String userLoginPassword = password.getText().toString();

        //HANDLING  EMPTY FIELDS
        if (TextUtils.isEmpty(userLoginEmail)){
            Toast.makeText(LoginActivity.this, "please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userLoginPassword)){
            Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        //FIREBASE SIGN IN USER BY EMAIL AND PASSWORD
        mAuth.signInWithEmailAndPassword(userLoginEmail, userLoginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onClick: trying to sign in with email and password");
                if (task.isSuccessful()) {
                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()){
                        startActivity(mainActivityIntent);
                        Log.d(TAG, "onComplete: signIn with email successful");
                    }else {
                        startActivity(verificationIntent);
                        Toast.makeText(LoginActivity.this, "please verify your email", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.w(TAG, "onComplete: signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "failed to sign in please check your details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TODO HANDLE WRONG INPUTS
    }

    //SIGN UP AUTHENTICATION
    public void btnSignUp(View view) {
        //START ACTIVITY INTENT
        //TODO LINK TO ACCOUNT SETTINGS
        final Intent intent = new Intent(this, VerificationActivity.class);

        //CONVERTING EDIT TEXT TO STRINGS
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        //HANDLING  EMPTY FIELDS
        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(LoginActivity.this, "please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        //FIREBASE CREATE USER BY EMAIL AND PASSWORD FUNCTION
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null){
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                }else{
                                    Log.d(TAG, "Email not sent");
                                }
                            }
                        });
                    }
                    startActivity(intent);
                    Log.d(TAG, "onComplete: created user with email successfully");
                } else {
                    Log.w(TAG, "onComplete: create user with email failed ", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to create user.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
