// CardListAdapter.kt
package com.example.AproManager.kotlinCode.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.dragAndDrop.ItemTouchHelperCallback

class CardListAdapter : RecyclerView.Adapter<CardListAdapter.CardViewHolder>() {

    private val cardList: MutableList<String> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun addCard(cardName: String) {
        cardList.add(cardName)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cardlist, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardName = cardList[position]
        holder.bind(cardName)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCardName: TextView = itemView.findViewById(R.id.cardName)
        private val cardRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerViewCard)
        private val addCardBtn: TextView = itemView.findViewById(R.id.addCardBtn)

        private val cardAdapter = CardAdapter()

        init {
            cardRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            cardRecyclerView.adapter = cardAdapter

            val itemTouchHelperCallback = ItemTouchHelperCallback(cardAdapter)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(cardRecyclerView)


            addCardBtn.setOnClickListener {
                showAddCardDialog()
            }
        }
        fun bind(cardName: String) {
            textViewCardName.text = cardName
        }
        private fun showAddCardDialog() {
            val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_add_card, null)
            val editTextCardName: EditText = dialogView.findViewById(R.id.editTextCardName)
            val buttonAddCard: Button = dialogView.findViewById(R.id.buttonAddCard)

            val alertDialogBuilder = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            buttonAddCard.setOnClickListener {
                val cardName = editTextCardName.text?.toString()?.trim()
                if (!cardName.isNullOrBlank()) {

                    cardAdapter.addCard(cardName)
                    alertDialog.dismiss()
                }
            }
        }
    }
    }

