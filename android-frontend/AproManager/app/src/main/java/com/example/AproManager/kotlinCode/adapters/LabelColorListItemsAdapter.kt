package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding= ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is MyViewHolder) {
            holder.bind(item)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.viewMain.setBackgroundColor(Color.parseColor(item))

            if (item == mSelectedColor) {
                binding.ivSelectedColor.visibility = View.VISIBLE
            } else {
                binding.ivSelectedColor.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClickListener?.onClick(adapterPosition, item)
            }
        }
    }


    interface OnItemClickListener {

        fun onClick(position: Int, color: String)
    }
}