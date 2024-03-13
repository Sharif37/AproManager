package com.example.AproManager.kotlinCode.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivityMainNewBinding
import com.example.AproManager.databinding.AppBarMainBinding
import com.example.AproManager.databinding.ContentMainBinding
import com.example.AproManager.javaCode.Activities.SignInActivity
import com.example.AproManager.kotlinCode.adapters.BoardItemsAdapter
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.Review
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.retrofit.RetrofitClient
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainNewBinding
    private lateinit var appBarMainBinding: AppBarMainBinding
    private lateinit var contentMain: ContentMainBinding
    // A global variable for User Name
    private lateinit var mUserName: String
    private lateinit var mSharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainNewBinding.inflate(layoutInflater)
        appBarMainBinding = binding.AppBarMain
        contentMain=binding.AppBarMain.contentMain

        setContentView(binding.root)

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)

        mSharedPreferences=this.getSharedPreferences(Constants.APROMANAGER_SHAREPREFERENCE,
            Context.MODE_PRIVATE)
        val tokenUpdated=mSharedPreferences.getBoolean(Constants.Fcm_Token_Updated,false)
        if(tokenUpdated){
            //showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseDatabaseClass().loadUserData(this,true)

        }else
        {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                updateFCMToken(token)
            }.addOnFailureListener { exception ->
                Log.e("FCM Token", "Failed to retrieve FCM token: ${exception.message}")

            }

        }


        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseDatabaseClass().loadUserData(this@MainActivity, true)

        appBarMainBinding.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivityForResult(
                    Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_about_us -> {
                   startActivity(  Intent(this@MainActivity, AboutUsActivity::class.java))
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_rate_app ->{
                showRateAppDialog()

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showRateAppDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_rate_app, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle("Rate My App")
        dialogBuilder.setMessage("Please rate our app and leave a review!")

        dialogBuilder.setPositiveButton("Submit") { dialog, whichButton ->
            var instanceOfReview: Review? =null
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
            val rating = ratingBar.rating
            val reviewEditText = dialogView.findViewById<EditText>(R.id.reviewEditText)
            val review = reviewEditText.text.toString()
            val sharedPrefs = this.getSharedPreferences(Constants.APROMANAGER_SHAREPREFERENCE, Context.MODE_PRIVATE)
            val profileUri=sharedPrefs.getString(Constants.profileUri, "") ?: ""




            // Store rating and review in Firebase Realtime Database
            val database = FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val ref = database.getReference("reviews")
            val reviewId = ref.push().key!!
            if (review.isNotEmpty()) {
                val currentTime= Date().time
                instanceOfReview = Review(reviewId, rating, review, getUserName(), currentTime, profileUri)
                reviewEditText.text.clear()
                
                //send review to rest API 
                sendReviewToRestApi(rating, review, getUserName(), currentTime, profileUri)

            } else {
                Toast.makeText(this, "Enter a review.", Toast.LENGTH_SHORT).show()
            }
           ref.child(reviewId).setValue(instanceOfReview)
            Toast.makeText(this, "Thank you for your review!", Toast.LENGTH_SHORT).show()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, whichButton ->
            // Cancel button clicked, do nothing
        }

        val b = dialogBuilder.create()
        b.show()
    }

    private fun sendReviewToRestApi(rating: Float, review: String, userName: String, currentTime: Long, profileUri: String) {
        val apiService = RetrofitClient.getApiService()
        val reviewObject=Review(rating, review,userName, currentTime, profileUri)
        val call:Call<ResponseBody> = apiService.sendReview(reviewObject)
            call.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        // Review sent successfully
                        Log.d("MainActivity", "Response: ${response.body()}")
                        Log.d("MainActivity", "Review sent successfully")
                    } else {
                        // Review sending failed
                        Log.e("MainActivity", "Failed to send review")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("MainActivity", "Error sending review", t)
                }


            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirebaseDatabaseClass().loadUserData(this@MainActivity)
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == CREATE_BOARD_REQUEST_CODE
        ) {
            // Get the latest boards list.
            FirebaseDatabaseClass().getBoardsList(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

//        setSupportActionBar(appBarMainBinding.toolbarMainActivity)
        appBarMainBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        appBarMainBinding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        hideProgressDialog()

        mUserName = user.name

        // The instance of the header view of the navigation view.
        val headerView = binding.navView.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)
        //Log.d("UserProfileUri", "User Profile URI: ${user.image}")

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name

        if (readBoardsList) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseDatabaseClass().getBoardsList(this@MainActivity)
        }
    }

    /**
     * A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
     */
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {

        hideProgressDialog()

        if (boardsList.size > 0) {

            contentMain.rvBoardsList.visibility = View.VISIBLE
            contentMain.tvNoBoardsAvailable.visibility = View.GONE

            contentMain.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            contentMain.rvBoardsList.setHasFixedSize(true)

            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            contentMain.rvBoardsList.adapter = adapter // Attach the adapter to the recyclerView.

            adapter.setOnClickListener(object :
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.boardId)
                    startActivity(intent)
                }
            })
        } else {
            contentMain.rvBoardsList.visibility = View.GONE
            contentMain.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor:SharedPreferences.Editor=mSharedPreferences.edit()
        editor.putBoolean(Constants.Fcm_Token_Updated,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseDatabaseClass().loadUserData(this,true)

    }
    private fun updateFCMToken(token:String){
        val userHashMap=HashMap<String,Any>()
        userHashMap[Constants.Fcm_Token]=token
      FirebaseDatabaseClass().updateUserProfileData(this,userHashMap)
    }

    /**
     * A companion object to declare the constants.
     */
    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11

        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }
}