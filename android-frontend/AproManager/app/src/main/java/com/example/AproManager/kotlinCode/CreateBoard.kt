package com.example.AproManager.kotlinCode

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.example.AproManager.R


class CreateBoard : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        val editTextBoardName: EditText = findViewById(R.id.editTextBoardName)
        val spinnerVisibility: Spinner = findViewById(R.id.spinnerVisibility)
        val buttonCreateBoard: Button = findViewById(R.id.buttonCreateBoard)
        val backgroundImage: ImageView = findViewById(R.id.backgroundImage)

        editTextBoardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                buttonCreateBoard.isEnabled = !s.isNullOrBlank()
            }
        })

        buttonCreateBoard.setOnClickListener {
            var boardName = editTextBoardName.text.toString()
            val resultIntent = Intent()
            resultIntent.putExtra("boardName", boardName)

            // Set the result code to indicate success
            setResult(Activity.RESULT_OK, resultIntent)

            // Finish the activity
            finish()
            editTextBoardName.text.clear()



        }







    }
}