package com.scalesandsoftware.bodypal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class VerificationActivity extends AppCompatActivity {

    final String TAG ="Verification Activity";
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        continueButton = findViewById(R.id.continueButton);
    }

    public void btnContinue(View view) {
        final Intent mainActivityIntent = new Intent(this, MainActivity.class);
        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()){
            startActivity(mainActivityIntent);
            Log.d(TAG, "onComplete: signIn with email successful");
        }else {
            Toast.makeText(this, "please verify your email", Toast.LENGTH_SHORT).show();
        }
    }
}
