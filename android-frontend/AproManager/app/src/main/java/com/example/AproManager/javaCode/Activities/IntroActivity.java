package com.example.AproManager.javaCode.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.AproManager.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Find views
        TextView tvAppNameIntro = findViewById(R.id.tv_app_name_intro);
        Button btnSignInIntro = findViewById(R.id.btn_sign_in_intro);
        Button btnSignUpIntro = findViewById(R.id.btn_sign_up_intro);



        // Set click listeners for buttons
        btnSignInIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IntroActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

        btnSignUpIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IntroActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
