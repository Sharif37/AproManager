package com.example.AproManager.kotlinCode.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.dragAndDrop.ItemTouchHelperAdapter
import java.util.Collections

class CardAdapter : RecyclerView.Adapter<CardAdapter.CardViewHolder>(), ItemTouchHelperAdapter {

    private val cardItemList: MutableList<String> = mutableListOf()

    fun addCard(cardName: String) {
        cardItemList.add(cardName)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardName = cardItemList[position]
        holder.bind(cardName)
    }

    override fun getItemCount(): Int {
        return cardItemList.size
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCardName: TextView = itemView.findViewById(R.id.innerCardName)

        fun bind(cardName: String) {
            textViewCardName.text = cardName

        }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(cardItemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        cardItemList.removeAt(position)
        notifyItemRemoved(position)
    }
}
