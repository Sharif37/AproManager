package com.example.AproManager.kotlinCode.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivityMyProfileBinding
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirebaseDatabaseClass().loadUserData(this@MyProfileActivity)

        binding.ivProfileUserImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    Toast.makeText(
                        this,
                        "Permission required to access images",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }


        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {

                uploadUserImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
                && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data!!

            try {
                // Load the user image in the ImageView.
                Glide
                        .with(this@MyProfileActivity)
                        .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                        .centerCrop() // Scale type of the image.
                        .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                        .into(binding.ivProfileUserImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for image access.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {

        mUserDetails = user

        Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }


    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "."
                            + Constants.getFileExtension(this@MyProfileActivity, mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get the downloadable url from the task snapshot
                        taskSnapshot.metadata!!.reference!!.downloadUrl
                                .addOnSuccessListener { uri ->
                                   // Log.e("Downloadable Image URL", uri.toString())

                                    // assign the image url to the variable.
                                    mProfileImageURL = uri.toString()

                                    // Call a function to update user details in the database.
                                    updateUserProfileData()
                                }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                                this@MyProfileActivity,
                                exception.message,
                                Toast.LENGTH_LONG
                        ).show()

                        hideProgressDialog()
                    }
        }
    }

    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData() {

        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {

            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (binding.etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding.etName.text.toString()
        }

        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
        }

        // Update the data in the database.
        FirebaseDatabaseClass()
            .updateUserProfileData(this@MyProfileActivity, userHashMap)
    }

    /**
     * A function to notify the user profile is updated successfully.
     */
    fun profileUpdateSuccess() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
}