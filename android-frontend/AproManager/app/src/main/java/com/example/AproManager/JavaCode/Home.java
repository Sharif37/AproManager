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
import com.getbase.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView ;

import java.util.ArrayList;
import java.util.List;


public class Home extends BaseActivity {

    private RecyclerView recyclerView;
    private BoardAdapter boardAdapter;
    private final List<Board> boardList = new ArrayList<>();

    private FloatingActionButton addBoard, addCard;
    private static final int CREATE_BOARD_REQUEST = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBoard=findViewById(R.id.showAddBoard);
        addCard=findViewById(R.id.showAddCard);
        recyclerView=findViewById(R.id.recyclerViewBoard);

        boardAdapter= new BoardAdapter(boardList);
        recyclerView.setAdapter(boardAdapter);
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

                var newBoard = new Board(boardName);
                boardList.add(newBoard);
                boardAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(boardList.size() - 1);

               

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