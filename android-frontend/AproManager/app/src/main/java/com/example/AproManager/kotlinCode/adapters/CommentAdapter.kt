package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.CommentItemsBinding
import com.example.AproManager.kotlinCode.models.Comments

     class CommentAdapter(
    private val context: Context,
    private val currentUserId: String,
    private var commentList: ArrayList<Comments>,
    private val likeClickListener: OnClickListener,
    private val dislikeClickListener: OnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val binding = CommentItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommentViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) {
            holder.bind(commentList[position])
        }
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

         inner class CommentViewHolder(private val binding: CommentItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Comments) {
            Glide
                .with(context)
                .load(model.userProfileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)

            binding.CommentedUserName.text = model.commentBy
            binding.UserComments.text = model.comment

            val commentTimestamp = model.timeStamp
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
            val dislikeCount=model.dislikedBy.size

            if(model.likedBy.contains(currentUserId)){
                binding.likeButton.setImageResource(R.drawable.thum_up_after_liked)
            }
            if(model.dislikedBy.contains(currentUserId)){
                binding.dislikeButton.setImageResource(R.drawable.baseline_thumb_down_after_dislike_24)
            }
            if(likeCount>0) {
                binding.likeCount.text = likeCount.toString()
                binding.likeCount.visibility = View.VISIBLE
            }
            if(dislikeCount>0){
                binding.dislikeCount.text = dislikeCount.toString()
                binding.dislikeCount.visibility =View.VISIBLE

            }
            binding.likeButton.setOnClickListener {
                likeClickListener.onLikeClick(bindingAdapterPosition,binding.likeCount,binding.likeButton)
            }
            binding.dislikeButton.setOnClickListener{
                dislikeClickListener.onDisLikeClick(bindingAdapterPosition,binding.dislikeCount,binding.dislikeButton)
            }


        }
    }



    interface OnClickListener {

        fun onLikeClick(position: Int, likeCountTextView: TextView, likeButton: ImageButton)
        fun onDisLikeClick(position: Int, disLikeCountTextView: TextView, dislikeButton: ImageButton)
    }


}
