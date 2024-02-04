package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ItemBoardNewBinding
import com.example.AproManager.kotlinCode.models.Board


open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding=ItemBoardNewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.bind(model)
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }


    inner class MyViewHolder(private val binding: ItemBoardNewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: Board) {
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.ivBoardImage)

            binding.tvName.text = model.name
            "Created By : ${model.createdBy}".also { binding.tvCreatedBy.text = it }

            binding.root.setOnClickListener {
                onClickListener?.onClick(adapterPosition, model)
            }


        }
    }
}