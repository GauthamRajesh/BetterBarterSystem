package com.gaucow.betterbartersystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.gaucow.betterbartersystem.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class SignIn extends AppCompatActivity {
    FirebaseAuth auth;
    private final int signInCode = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        init();
    }
    private void init() {
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Toast.makeText(this, "Already signed in.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startSignInFlow();
        }
    }
    private void startSignInFlow() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setTosAndPrivacyPolicyUrls("https://example.com",
                "https://example.com")
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.Theme_BetterBarterSystem)
                        .build(),
                signInCode);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == signInCode) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                if (response == null) {
                    showToast(getString(R.string.signin_cancelled));
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast(getString(R.string.no_internet));
                    return;
                }
                showToast(getString(R.string.unknown_error));
            }
        }
    }
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
