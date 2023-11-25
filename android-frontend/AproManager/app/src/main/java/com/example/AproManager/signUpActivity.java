package com.example.AproManager;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signUpActivity extends AppCompatActivity {

    Button signUp;
    CardView signUp_google, signUp_microsoft;
    private  EditText userEmail, userPassword;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 3;
    SignInCredential credential ;
    private boolean showOneTapUI = true;
    // UUID random ;

    ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        signUp = findViewById(R.id.signupbtn);
        signUp_google = findViewById(R.id.card_google);
        apiService = ApiService.getInstance();
        userEmail=findViewById(R.id.email);
        userPassword=findViewById(R.id.password);


        signUp_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingSpinner.showLoadingSpinner(signUpActivity.this);
                oneTapClient = Identity.getSignInClient(signUpActivity.this);
                BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.clientID))
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                        .build();

                GoogleOneTapUtils.startOneTapLogin(signUpActivity.this, oneTapClient, signInRequest, REQ_ONE_TAP);


            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password ;
                email=userEmail.getText().toString();
                password=userPassword.getText().toString();
                if(GoogleOneTapUtils.isValidEmail(email))
                {
                    if(password.length()>=5)
                    {
                       checkUser(email,password,null);

                    }else
                    {
                        Toast.makeText(signUpActivity.this, "password minimum length should 5", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    Toast.makeText(signUpActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        // Toast.makeText(this, "got id token", Toast.LENGTH_SHORT).show();

                        // Here, you can send the ID token to your backend for validation.

                        // For example, you can create a UserData object and send it to the server:
                        String email = credential.getId();
                        checkUser(email, null,credential);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "API exception: " + e.getStatusCode());
                }finally {
                    loadingSpinner.hideLoadingSpinner();
                }
                break;
        }

    }

    public void checkUser(String email,String password, SignInCredential credential) {
        GoogleOneTapUtils.checkUser(this, apiService, email,password,credential);
    }






}
