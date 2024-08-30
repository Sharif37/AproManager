package com.example.AproManager.kotlinCode.activities

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.AproManager.databinding.GenerateImageBinding
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.retrofit.ApiService
import com.example.AproManager.kotlinCode.retrofit.ImageRequest
import com.example.AproManager.kotlinCode.retrofit.RetrofitClient2
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class GenerateImageActivity : AppCompatActivity() {
    private lateinit var storageReference: StorageReference
    private val AUTH_TOKEN: String = "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"
    private var imageFile: File? = null
    private var mDescriptionImageURL: String = ""
    private lateinit var binding: GenerateImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = GenerateImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageReference = FirebaseStorage.getInstance().reference

        binding.textToImageButton.setOnClickListener {
            binding.ivGeneratedImage.setImageDrawable(null)
            binding.loadingSpinner.visibility = View.VISIBLE
            val text = binding.textToImageText.text.toString()
            generateImage(text)
            binding.textToImageText.text = null
        }

        binding.btnAddToProject.setOnClickListener {

            imageFile?.let {
                uploadImageToFirebase()
            } ?: run {
                Log.e(TAG, "Image file is null")
                // Optionally show a toast or alert to the user
            }
        }
    }

    private fun generateImage(text: String) {
        val apiService: ApiService =
            RetrofitClient2.getClient(AUTH_TOKEN).create(ApiService::class.java)
        val request = ImageRequest(text)

        apiService.generateImage(request)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->

                        // Save the image to a file
                        val time = Date().time
                        imageFile = File(baseContext.cacheDir, "generated_image-$time.jpg")
                        try {
                            FileOutputStream(imageFile).use { outputStream ->
                                responseBody.byteStream().copyTo(outputStream)
                            }
                            Log.d(TAG, "Image saved to ${imageFile!!.absolutePath}")

                            // Using Glide to load the image
                            Glide.with(this@GenerateImageActivity)
                                .load(imageFile)
                                .into(binding.ivGeneratedImage)

                            binding.ivGeneratedImage.visibility = View.VISIBLE
                            binding.btnAddToProject.visibility = View.VISIBLE
                            binding.loadingSpinner.visibility = View.GONE

                        } catch (e: IOException) {
                            Log.e(TAG, "Error saving image", e)
                            binding.loadingSpinner.visibility = View.GONE
                            binding.btnAddToProject.visibility = View.GONE
                        }
                    } ?: run {
                        Log.e(TAG, "Response body is null")
                        binding.loadingSpinner.visibility = View.GONE
                        binding.btnAddToProject.visibility = View.GONE
                    }
                } else {
                    Log.e(TAG, "Response error: ${response.errorBody()?.string()}")
                    binding.loadingSpinner.visibility = View.GONE
                    binding.btnAddToProject.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "API call failed: ", t)
                binding.loadingSpinner.visibility = View.GONE
                binding.btnAddToProject.visibility = View.GONE
            }
        })
    }

    private fun uploadImageToFirebase() {
        val fileName = "generated_image_${Date().time}.jpg"
        val imageRef = storageReference.child("description image/$fileName")

        imageRef.putFile(Uri.fromFile(imageFile))
            .addOnSuccessListener {
                Log.d(TAG, "Image uploaded successfully to ${imageRef.path}")
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d(TAG, "Image URL: $uri")
                    mDescriptionImageURL = uri.toString()
                    sendResult()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Image upload failed", e)

            }
    }


    private fun sendResult() {
        val boardDetails: Board? = intent.getParcelableExtra(Constants.BOARD_DETAIL)
        val taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        val cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        val membersList: ArrayList<User>? =
            intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)

        val resultIntent = Intent().apply {
            putExtra(Constants.Image_URL, imageFile?.absolutePath.toString())
            putExtra(Constants.BOARD_DETAIL, boardDetails)
            putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
            putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
            putExtra(Constants.BOARD_MEMBERS_LIST, membersList)
            putExtra(Constants.DESCRIPTION, mDescriptionImageURL)
            Log.d(TAG, "Image URL sent: $mDescriptionImageURL")
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


}