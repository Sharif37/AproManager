
package com.example.AproManager.javaCode.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.AproManager.R;
import com.example.AproManager.kotlinCode.activities.BaseActivity;
import com.example.AproManager.kotlinCode.firebase.FirestoreClass;
import com.example.AproManager.kotlinCode.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends BaseActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_new);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );



        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignUp = findViewById(R.id.btn_sign_up);

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
                                String registeredEmail = firebaseUser.getEmail();

                                User user = new User(firebaseUser.getUid(), name, registeredEmail, "", 0, "", false);

                                FirestoreClass firestoreClass = new FirestoreClass();
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
        } else {
            return true;
        }
    }

    public void userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered.", Toast.LENGTH_SHORT).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
