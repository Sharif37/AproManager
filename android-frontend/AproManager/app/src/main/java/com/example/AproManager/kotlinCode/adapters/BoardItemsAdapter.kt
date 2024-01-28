package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
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

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding=ItemBoardNewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
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

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(private val binding: ItemBoardNewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: Board) {
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.ivBoardImage)

            binding.tvName.text = model.name
            binding.tvCreatedBy.text = "Created By : ${model.createdBy}"

            binding.root.setOnClickListener {
                onClickListener?.onClick(adapterPosition, model)
            }


        }
    }
}