package com.example.AproManager.kotlinCode.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AproManager.databinding.ActivityAboutUsBinding
import com.example.AproManager.kotlinCode.GraphQl.RetrofitClientForGraphQl
import com.example.AproManager.kotlinCode.adapters.ReviewAdapter
import com.example.AproManager.kotlinCode.models.GraphQLRequest
import com.example.AproManager.kotlinCode.models.Review
import com.example.AproManager.kotlinCode.retrofit.RetrofitClient
import com.example.AproManager.kotlinCode.retrofit.RetrofitClient2
import com.google.firebase.database.*
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUsActivity : AppCompatActivity() {

    private lateinit var adapter: ReviewAdapter
    private var reviewList: ArrayList<Review> = ArrayList()
    private lateinit var binding: ActivityAboutUsBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.mapWebView.settings.javaScriptEnabled = true

        loadVideo()
        loadMap()

        setupReviewSourceSelector()

        binding.fetchDataButton.setOnClickListener {
            val selectedSource = binding.dataSourceSpinner.selectedItem.toString()
            when (selectedSource) {
                "Firebase" -> getAllReviewsFromDatabase { reviews ->
                    displayReviews(reviews)
                }
                "REST API" -> getAllReviewsFromRestApi { reviews, throwable ->
                    if (reviews != null) {
                        displayReviews(reviews)
                    } else {
                        showError(throwable)
                    }
                }
                "GraphQL" -> getAllReviewsFromGraphQl { reviews, throwable ->
                    if (reviews != null) {
                        displayReviews(reviews)
                    } else {
                        showError(throwable)
                    }
                }
                else -> Toast.makeText(this, "Please select a valid source", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupReviewSourceSelector() {
        val sources = listOf("Select Source", "Firebase", "REST API", "GraphQL")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sources)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dataSourceSpinner.adapter = adapter
    }

    private fun displayReviews(reviews: ArrayList<Review>) {
        adapter = ReviewAdapter(reviews, this@AboutUsActivity)
        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reviewRecyclerView.adapter = adapter
    }

    private fun showError(throwable: Throwable?) {
        Toast.makeText(this, "Error fetching reviews: ${throwable?.message}", Toast.LENGTH_LONG).show()
        Log.e("AboutUsActivity", "Error fetching reviews", throwable)
    }

    private fun loadVideo() {
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

    private fun loadMap() {
        val frameMap = """
            <iframe src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d460.8617979104278!2d91.7856623!3d22.4706029!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x30acd6fcf93fffff%3A0x12b289338778d80f!2sIT%20Building!5e0!3m2!1sen!2sbd!4v1709773506963!5m2!1sen!2sbd" width="400" height="300" style="border:0;" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
        """.trimIndent()
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
                callback(reviewList)
            }
        })
    }

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
                    callback(null, Exception("Failed to fetch reviews: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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

    private fun getAllReviewsFromGraphQl(callback: (ArrayList<Review>?, Throwable?) -> Unit) {
        val query = """
            query {
                getReviews {
                    id
                    rating
                    review
                    reviewBy
                    reviewTime
                    profileUri
                }
            }
        """.trimIndent()

        val service = RetrofitClientForGraphQl.getApiService()
        val call = service.postQuery(GraphQLRequest(query, emptyMap()))

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val reviewList = ArrayList<Review>()
                    val data = response.body()?.getAsJsonObject("data")
                    val reviews = data?.getAsJsonArray("getReviews")

                    reviews?.forEach { reviewJson ->
                        val reviewObject = reviewJson.asJsonObject
                        val review = Review(
                            reviewId = reviewObject.get("id").asString,
                            rating = reviewObject.get("rating").asFloat,
                            review = reviewObject.get("review").asString,
                            reviewBy = reviewObject.get("reviewBy").asString,
                            reviewTime = reviewObject.get("reviewTime").asLong,
                            profileUri = reviewObject.get("profileUri").asString
                        )
                        reviewList.add(review)
                    }

                    callback(reviewList, null)
                } else {
                    callback(null, Exception("Failed to fetch reviews: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback(null, t)
            }
        })
    }
}
