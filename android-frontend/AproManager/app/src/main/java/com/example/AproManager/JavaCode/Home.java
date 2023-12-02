package com.example.AproManager.JavaCode;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.AproManager.R;
import com.example.AproManager.kotlinCode.Board;
import com.example.AproManager.kotlinCode.BoardAdapter;
import com.example.AproManager.kotlinCode.CreateBoard;
import com.example.AproManager.kotlinCode.WorkSpaceAdapter;
import com.example.AproManager.kotlinCode.Workspace;
import com.getbase.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView ;

import java.util.ArrayList;
import java.util.List;


public class Home extends BaseActivity {

    private RecyclerView recyclerView;
    private WorkSpaceAdapter workspaceAdapter;
    private final List<Workspace> workspaceList = new ArrayList<>();

    private FloatingActionButton addBoard, addCard;
    private static final int CREATE_BOARD_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBoard=findViewById(R.id.showAddBoard);
        addCard=findViewById(R.id.showAddCard);
        recyclerView=findViewById(R.id.recyclerViewBoard);

        workspaceAdapter = new WorkSpaceAdapter(workspaceList);
        recyclerView.setAdapter(workspaceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this, CreateBoard.class);
                startActivityForResult(intent, CREATE_BOARD_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_BOARD_REQUEST && resultCode == RESULT_OK && data != null) {
            String boardName = data.getStringExtra("boardName");

            if (boardName != null && !boardName.isEmpty()) {

                List<Board> boards1 = new ArrayList<>();
                boards1.add(new Board("Board 1"));
                boards1.add(new Board("Board 2"));
                workspaceList.add(new Workspace("Workspace 1", boards1));

                List<Board> boards2 = new ArrayList<>();
                boards2.add(new Board("Board 3"));
                boards2.add(new Board("Board 4"));
                workspaceList.add(new Workspace("Workspace 2", boards2));

                workspaceAdapter.notifyDataSetChanged();


            }
        }
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.refresh ;
    }


    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }




}