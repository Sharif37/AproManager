package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.CommentItemsBinding
import com.example.AproManager.kotlinCode.models.Comments
import com.example.AproManager.kotlinCode.utils.Constants

open class CommentAdapter(
    private  val context: Context,
    private var commentList:ArrayList<Comments>,
    private val likeClickListener: OnLikeClickListener
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): RecyclerView.ViewHolder {
val binding=CommentItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentViewHolder(binding)
    }



    override fun getItemCount(): Int {
       return commentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=commentList[position]
        if(holder is CommentViewHolder){
            holder.bind(model)
        }
    }


    inner class CommentViewHolder(private val binding: CommentItemsBinding) : RecyclerView.ViewHolder(binding.root) {

        val sharedPrefs = context.getSharedPreferences(Constants.APROMANAGER_SHAREPREFERENCE, Context.MODE_PRIVATE)
        private val profileUri=sharedPrefs.getString(Constants.profileUri, "") ?: ""

        fun bind(model: Comments) {
            Glide
                .with(context)
                .load(profileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)

            binding.CommentedUserName.text = model.commentBy
            binding.UserComments.text = model.comment

            val commentTimestamp = model.timeStamp.time
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - commentTimestamp


            val secondsDifference = timeDifference / 1000
            val minutesDifference = secondsDifference / 60
            val hoursDifference = minutesDifference / 60
            val daysDifference = hoursDifference / 24
            val weeksDifference = daysDifference / 7
            val yearsDifference = daysDifference / 365

            // Set the text based on the difference
            val timeAgoText = when {
                yearsDifference > 0 -> "${yearsDifference}y"
                weeksDifference > 0 -> "${weeksDifference}w"
                daysDifference > 0 -> "${daysDifference}d"
                hoursDifference > 0 -> "${hoursDifference}h"
                minutesDifference > 0 -> "${minutesDifference}m"
                else -> "${secondsDifference}s"
            }

            binding.commentTime.text = timeAgoText

           val likeCount=model.likedBy.size
            if(likeCount>0) {
                binding.likeCount.text = likeCount.toString()
                binding.likeCount.visibility =View.VISIBLE
            }
            binding.likeButton.setOnClickListener {
                likeClickListener.onLikeClick(adapterPosition,binding.likeCount)
            }


        }
    }


    interface OnLikeClickListener {
        fun onLikeClick(position: Int, likeCountTextView: TextView)
    }



}