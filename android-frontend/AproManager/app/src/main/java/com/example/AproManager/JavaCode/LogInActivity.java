package com.example.AproManager.JavaCode;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AproManager.R;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {


    CardView loginWithgoogle ;
    TextView signup ;
    private static final int REQ_ONE_TAP = 2;
    SignInClient oneTapClient ;
    SignInCredential credential ;
    ApiService apiService ;
    private  EditText userEmail, userPassword;
    Button  logInBtn ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_log_in);
        getSupportActionBar().hide();

        String userLogIn = GoogleOneTapUtils.getUserSession(this);

        if (userLogIn != null) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        } else {

            setContentView(R.layout.activity_log_in);


            loginWithgoogle = findViewById(R.id.card_google);
            signup = findViewById(R.id.signUpText);
            apiService = ApiService.getInstance();
            userEmail = findViewById(R.id.email);
            userPassword = findViewById(R.id.password);
            logInBtn = findViewById(R.id.login_btn);


            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LogInActivity.this, signUpActivity.class);
                    startActivity(intent);
                }
            });

            loginWithgoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    loadingSpinner.showLoadingSpinner(LogInActivity.this);

                    oneTapClient = Identity.getSignInClient(LogInActivity.this);

                    GoogleOneTapUtils.openOneTap(LogInActivity.this, REQ_ONE_TAP);

                }
            });
            logInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email, password;
                    email = userEmail.getText().toString();
                    password = userPassword.getText().toString();
                    if (GoogleOneTapUtils.isValidEmail(email)) {
                        if (password.length() >= 5) {
                            GoogleOneTapUtils.login_usingPass(LogInActivity.this, email, password);

                        } else {
                            Toast.makeText(LogInActivity.this, "password minimum length should 5", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(LogInActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQ_ONE_TAP) {
            try {
                credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {

                    String email = credential.getId();
                    GoogleOneTapUtils.login_usingGoogle(LogInActivity.this, email);

                }
            } catch (ApiException e) {
                Log.e(TAG, "API exception: " + e.getStatusCode());
            } finally {
                // Hide  spinner
                loadingSpinner.hideLoadingSpinner();
            }
        }


    }




}