
package com.example.AproManager.javaCode.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;


import com.example.AproManager.R;
import com.example.AproManager.kotlinCode.activities.BaseActivity;
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass;
import com.example.AproManager.kotlinCode.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends BaseActivity {
    private EditText etName, etEmail, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_new);

        FirebaseApp.initializeApp(this);



        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );



        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnSignUp = findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();
            }
        });


    }



    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateForm(name, email, password)) {
            showProgressDialog(getResources().getString(R.string.please_wait));
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                String registeredEmail = firebaseUser.getEmail();

                                User user = new User(firebaseUser.getUid(), name, registeredEmail, "", 0, "", false);

                                FirebaseDatabaseClass firestoreClass = new FirebaseDatabaseClass();
                                //TODO handle user registration
                                firestoreClass.registerUser(SignUpActivity.this, user);
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validateForm(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar("Please enter name.");
            return false;
        } else if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.");
            return false;
        }else if(isEmailValid(email)){
            showErrorSnackBar("Please enter valid email.");
            return false ;
        }

        else {
            return true;
        }
    }


    public boolean isEmailValid(String email) {
        // Define a regular expression for the Android email format
        String regex = "^[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+$";

        // Use the regular expression to match the email address
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public void userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered.", Toast.LENGTH_SHORT).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
