package com.example.AproManager.kotlinCode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R

class BoardAdapter(private val boards:List<Board>) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    class BoardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            fun bind(board : Board) {
                val textViewBoardName : TextView =itemView.findViewById(R.id.textViewBoardName)
                textViewBoardName.text=board.name
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_board,parent,false)
        return BoardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return boards.size
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board=boards[position]
        holder.bind(board)
    }

}