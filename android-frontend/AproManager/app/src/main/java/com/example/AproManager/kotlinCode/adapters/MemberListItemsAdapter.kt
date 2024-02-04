package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ItemMemberBinding
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        fun onClick(position: Int, user: User, action: String)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(private val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: User) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivMemberImage)

            binding.tvMemberName.text = model.name
            binding.tvMemberEmail.text = model.email

            if (model.selected) {
                binding.ivSelectedMember.visibility = View.VISIBLE
            } else {
                binding.ivSelectedMember.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener?.onClick(adapterPosition, model, Constants.SELECT)
                } else {
                    onClickListener?.onClick(adapterPosition, model, Constants.UN_SELECT)

                }
            }
        }

    }
}