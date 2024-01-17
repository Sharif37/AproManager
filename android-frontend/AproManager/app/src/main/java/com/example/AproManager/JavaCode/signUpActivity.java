package com.example.AproManager.JavaCode;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.AproManager.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class signUpActivity extends AppCompatActivity {

    Button signUp;
    CardView signUp_google, signUp_microsoft;
    private  EditText userEmail, userPassword;
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 3;
    SignInCredential credential ;
    private final boolean showOneTapUI = true;
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

                GoogleOneTapUtils.openOneTap(signUpActivity.this,REQ_ONE_TAP);


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
                    GoogleOneTapUtils.signup_usingPass(signUpActivity.this,email,password);

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
                    //Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();
                    credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        String email = credential.getId();
                        String name = credential.getDisplayName();
                        assert credential.getProfilePictureUri() != null;
                        String profileLink = credential.getProfilePictureUri().toString();
                        //Log.d("profileLink",profileLink);
                       // Toast.makeText(this, profileLink.length()+"", Toast.LENGTH_SHORT).show();
                        long currentTimeMillis = System.currentTimeMillis();
                        Date currentDate = new Date(currentTimeMillis);
                        // Format the date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
                        String formattedDate = dateFormat.format(currentDate);


                        GoogleOneTapUtils.signup_usingGoogle(this,name,email,"-1",profileLink,formattedDate);

                    }
                } catch (ApiException e) {
                    Log.e(TAG, "API exception: " + e.getStatusCode());
                }finally {
                    loadingSpinner.hideLoadingSpinner();
                }
                break;
        }

    }







}
