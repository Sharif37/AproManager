package com.example.AproManager.kotlinCode.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.adapters.CardListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CardActivity : AppCompatActivity() {

    private lateinit var recyclerViewBoards: RecyclerView
    private lateinit var addListButton: FloatingActionButton
    private lateinit var cardListAdapter: CardListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        recyclerViewBoards = findViewById(R.id.recyclerViewCardLists)
        addListButton = findViewById(R.id.AddList)


        cardListAdapter = CardListAdapter()
        recyclerViewBoards.adapter = cardListAdapter
        recyclerViewBoards.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        addListButton.setOnClickListener {
            showAddCardListDialog()
        }

        val boardName = intent.getStringExtra("boardName")
        title = boardName
    }

         private fun showAddCardListDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_card_list, null)
        val editTextCardName: EditText = dialogView.findViewById(R.id.editTextListName)
        val buttonAddCard: Button = dialogView.findViewById(R.id.buttonAddCardList)

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        buttonAddCard.setOnClickListener {
            val cardName = editTextCardName.text?.toString()?.trim()
            if (!cardName.isNullOrBlank()) {
                cardListAdapter.addCard(cardName)
                alertDialog.dismiss()
            }
        }
    }



}
