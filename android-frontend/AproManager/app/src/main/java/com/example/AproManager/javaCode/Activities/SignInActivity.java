package com.example.AproManager.javaCode.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.AproManager.R;
import com.example.AproManager.kotlinCode.activities.BaseActivity;
import com.example.AproManager.kotlinCode.activities.MainActivity;
import com.example.AproManager.kotlinCode.firebase.FirestoreClass;
import com.example.AproManager.kotlinCode.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends BaseActivity {
    private EditText etEmail, etPassword;
    private Toolbar toolbar;
    private Button btnSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );



        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        toolbar = findViewById(R.id.toolbar_sign_in_activity);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInRegisteredUser();
            }
        });


    }



    private void signInRegisteredUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateForm(email, password)) {
            showProgressDialog(getResources().getString(R.string.please_wait));

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            new FirestoreClass().loadUserData(SignInActivity.this,false);
                        } else {
                            Toast.makeText(SignInActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.");
            return false;
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.");
            return false;
        } else {
            return true;
        }
    }

    public void signInSuccess(User user) {
        hideProgressDialog();

        startActivity(new Intent(SignInActivity.this, MainActivity.class));

        finish();
    }
}

