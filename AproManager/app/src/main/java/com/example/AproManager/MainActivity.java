package com.example.AproManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.recyclerview.widget.RecyclerView;




public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton btnOpenDialog;
    Button btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         recyclerView = findViewById(R.id.cardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        btnOpenDialog = (FloatingActionButton) findViewById(R.id.btnAddWorkspace);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_button_workspace);

        btnClose = dialog.findViewById(R.id.closeButton);

        btnOpenDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setWindowAnimations(R.style.AnimationsForDialog);
                dialog.show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "closed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }
    }
