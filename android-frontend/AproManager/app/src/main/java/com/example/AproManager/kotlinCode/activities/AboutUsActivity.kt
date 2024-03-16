package com.example.AproManager.kotlinCode.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AproManager.databinding.ActivityAboutUsBinding
import com.example.AproManager.kotlinCode.adapters.ReviewAdapter
import com.example.AproManager.kotlinCode.models.Review
import com.example.AproManager.kotlinCode.retrofit.RetrofitClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AboutUsActivity : AppCompatActivity() {



    private lateinit var adapter: ReviewAdapter
    private var reviewList:ArrayList<Review> = ArrayList()

    private lateinit var binding:ActivityAboutUsBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.webView.settings.javaScriptEnabled = true
        binding.mapWebView.settings.javaScriptEnabled = true

        loadVideo()
        loadMap()

        
        val avgRatingTextView = binding.avgRating
        val totalUserGiveRateTextView = binding.totalUserGiveRate

        // Bind RatingBar
        val ratingBar = binding.ratingBar

        // Bind ProgressBars
        val progressBar5 = binding.progressBar5
        val progressBar4 = binding.progressBar4
        val progressBar3 = binding.progressBar3
        val progressBar2 = binding.progressBar2
        val progressBar1 = binding.progressBar1



        getAllReviewsFromDatabase { reviewList ->

            val averageRating = reviewList.map { it.rating }.average()
            avgRatingTextView.text = String.format("%.1f", averageRating)
            ratingBar.rating= averageRating.toFloat()

            // Update total user rating count
            val totalUserRatingCount = reviewList.size
            totalUserGiveRateTextView.text = totalUserRatingCount.toString()

            // Calculate rating distribution
            val ratings = reviewList.map { it.rating.toInt() }
            val ratingCounts = IntArray(5) // Array to store count of ratings for each star (0-4 stars)

            for (rating in ratings) {
                ratingCounts[rating - 1]++ // Increment count for corresponding rating
            }

            // Update progress bars for each rating
            progressBar5.progress = calculateProgressBarProgress(ratingCounts[4], totalUserRatingCount)
            progressBar4.progress = calculateProgressBarProgress(ratingCounts[3], totalUserRatingCount)
            progressBar3.progress = calculateProgressBarProgress(ratingCounts[2], totalUserRatingCount)
            progressBar2.progress = calculateProgressBarProgress(ratingCounts[1], totalUserRatingCount)
            progressBar1.progress = calculateProgressBarProgress(ratingCounts[0], totalUserRatingCount)


            adapter=ReviewAdapter(reviewList,this@AboutUsActivity)
            binding.reviewRecyclerView.layoutManager=LinearLayoutManager(this)
            binding.reviewRecyclerView.adapter=adapter
        }


        getAllReviewsFromRestApi { reviews, throwable ->
            Log.d("AboutUsActivity",reviews.toString())
            if(reviews!=null) {
                adapter = ReviewAdapter(reviews, this@AboutUsActivity)
                binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.reviewRecyclerView.adapter = adapter
            }else
            {
                Log.d("AboutUsActivity",throwable.toString())
            }

        }





    }


    private fun calculateProgressBarProgress(ratingCount: Int, totalUserRatingCount: Int): Int {
        return if (totalUserRatingCount > 0) {
            ((ratingCount.toFloat() / totalUserRatingCount) * 100).toInt()
        } else {
            0
        }
    }

    private fun loadVideo()
    {
        val videoId = "Bcid33tgq8A"
        val frameVideo = """
    <!DOCTYPE html>
    <html>
    <body>
    <iframe width="400dp" height="300dp" src="https://www.youtube.com/embed/$videoId" frameborder="0" allow="autoplay; encrypted-media" splashscreen></iframe>
    </body>
    </html>
""".trimIndent()

        binding.webView.loadData(frameVideo, "text/html", "utf-8")
    }

    private fun loadMap()
    {
        val frameMap = """<iframe src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d460.8617979104278!2d91.7856623!3d22.4706029!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x30acd6fcf93fffff%3A0x12b289338778d80f!2sIT%20Building!5e0!3m2!1sen!2sbd!4v1709773506963!5m2!1sen!2sbd" width="400" height="300" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>"""
       binding.mapWebView.loadData(frameMap, "text/html", "utf-8")
    }

    private fun getAllReviewsFromDatabase(callback: (ArrayList<Review>) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val ref = database.getReference("reviews")

        val reviewList = ArrayList<Review>()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val review = snapshot.getValue(Review::class.java)
                    review?.let {
                        reviewList.add(it)
                    }
                }
                callback(reviewList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                callback(reviewList) // Return empty list in case of error
            }
        })
    }


   /* private fun getAllReviewsFromRestApi(callback: (ArrayList<Review>?, Throwable?) -> Unit) {
        val apiService = RetrofitClient.getApiService()

        val call: Call<ArrayList<Review>> = apiService.getAllReviews()

        call.enqueue(object : Callback<ArrayList<Review>> {
            override fun onResponse(call: Call<ArrayList<Review>>, response: Response<ArrayList<Review>>) {
                if (response.isSuccessful) {
                    val reviews = response.body()
                    callback(reviews, null)
                } else {
                    callback(null, Exception("Failed to fetch reviews: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ArrayList<Review>>, t: Throwable) {
                callback(null, t)
            }
        })
    }
*/


    private fun getAllReviewsFromRestApi(callback: (ArrayList<Review>?, Throwable?) -> Unit) {
        val apiService = RetrofitClient.getApiService()

        val call: Call<ResponseBody> = apiService.getAllReviews()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawJson = response.body()?.string()
                    val reviews = parseReviewsFromJson(rawJson)
                    callback(reviews, null)
                } else {
                    Log.e("getAllReviewsFromRestApi", "Failed to fetch reviews: ${response.code()}")
                    callback(null, Exception("Failed to fetch reviews: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("getAllReviewsFromRestApi", "Error fetching reviews", t)
                callback(null, t)
            }
        })
    }

    private fun parseReviewsFromJson(json: String?): ArrayList<Review> {
        val reviews = ArrayList<Review>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val reviewId = jsonObject.optString("review_id")
                val rating = jsonObject.optDouble("rating", 0.0).toFloat()
                val review = jsonObject.optString("review")
                val reviewBy = jsonObject.optString("review_by")
                val reviewTime = jsonObject.optLong("review_time")
                val profileUri = jsonObject.optString("profile_uri")
                val reviewObj = Review(reviewId, rating, review, reviewBy, reviewTime, profileUri)
                reviews.add(reviewObj)
            }
        } catch (e: JSONException) {
            Log.e("parseReviewsFromJson", "Error parsing JSON", e)
        }
        return reviews
    }

}