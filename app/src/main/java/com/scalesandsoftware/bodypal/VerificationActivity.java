package com.scalesandsoftware.bodypal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class VerificationActivity extends AppCompatActivity {

    final String TAG ="Verification Activity";
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        auth = FirebaseAuth.getInstance();

    }

    public void btnContinue(View view) {
        final Intent mainActivityIntent = new Intent(this, MainActivity.class);
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(getIntent().getStringExtra("Email")),
                    Objects.requireNonNull(getIntent().getStringExtra("password")));
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (user.isEmailVerified()){
                            Log.d(TAG, "onComplete: email verified");
                            startActivity(mainActivityIntent);
                        }else {
                            Log.d(TAG, "onComplete: email is not verified");
                            Toast.makeText(VerificationActivity.this, "please verify email", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d(TAG, "onComplete: re authentication failed");
                    }
                }
            });
        }
    }
}
