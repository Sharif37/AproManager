package com.example.AproManager.kotlinCode.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.interfaces.OnItemClickListener
import com.example.AproManager.kotlinCode.models.Board

class BoardAdapter(private val boards:List<Board>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
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
        holder.itemView.setOnClickListener{
            onItemClickListener.onItemClick(board)
        }
    }

}