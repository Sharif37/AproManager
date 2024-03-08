package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ReviewItemBinding
import com.example.AproManager.kotlinCode.models.Review

class ReviewAdapter(
    private var reviewList:ArrayList<Review>,
    private var context: Context
) : RecyclerView.Adapter<ReviewAdapter.ReviewInnerClass>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewInnerClass {
      val binding=ReviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReviewInnerClass(binding)
    }

    override fun getItemCount(): Int {
       return reviewList.size
    }

    override fun onBindViewHolder(holder: ReviewInnerClass, position: Int) {
        val model=reviewList[position]
        holder.bind(model)
    }

    inner class ReviewInnerClass(private var binding: ReviewItemBinding):ViewHolder(binding.root){
        fun bind(model: Review) {

            Glide
                .with(context)
                .load(model.profileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)


            binding.reviewerUserName.text=model.reviewBy
            binding.UserReview.text=model.review


            val reviewTimestamp = model.reviewTime
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - reviewTimestamp


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

            binding.reviewTime.text = timeAgoText

        }


    }


}
