package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ItemBoardNewBinding
import com.example.AproManager.databinding.ItemCardSelectedMemberBinding
import com.example.AproManager.kotlinCode.models.SelectedMembers



open class CardMemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>
    ,private val assignMembers:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding= ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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
        fun onClick()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(private val binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SelectedMembers) {
            if (adapterPosition == list.size - 1 && assignMembers) {
                binding.ivAddMember.visibility = View.VISIBLE
                binding.ivSelectedMemberImage.visibility = View.GONE
            } else {
                binding.ivAddMember.visibility = View.GONE
                binding.ivSelectedMemberImage.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivSelectedMemberImage)
            }

            binding.root.setOnClickListener {
                if(onClickListener!=null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }

}
