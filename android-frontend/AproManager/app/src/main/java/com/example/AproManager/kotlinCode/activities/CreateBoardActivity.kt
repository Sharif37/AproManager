package com.example.AproManager.kotlinCode.activities


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivityCreateBoardNewBinding
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding:ActivityCreateBoardNewBinding
    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for Username
    private lateinit var mUserName: String

    // A global variable for a board image URL
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardNewBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@CreateBoardActivity)
            } else {
                // Check if the permission should be requested again
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    // Permission has been denied previously, show rationale to the user
                    Toast.makeText(
                        this,
                        "Permission required to access images",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Permission has not been requested before or permanently denied, request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }

        binding.btnCreate.setOnClickListener {

            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {
                     uploadBoardImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))

                //  update create a board.
                createBoard()
            }
        }
    }

    /**
     * This function will notify the user after tapping on allow or deny
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@CreateBoardActivity)
            } else {
                // Display a toast or take any other action when permission is not granted
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for image access.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                // Load the board image in the ImageView.
                Glide
                    .with(this@CreateBoardActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(binding.ivBoardImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to upload the Board Image to storage and getting the downloadable URL of the image.
     */
    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(this@CreateBoardActivity, mSelectedImageFileUri)
        )

        //adding the file to reference
        sRef.putFile(mSelectedImageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // assign the image url to the variable.
                        mBoardImageURL = uri.toString()

                        // Call a function to create the board.
                        createBoard()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@CreateBoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
    }

    /**
     * A function to make an entry of a board in the database.
     */
    private fun createBoard() {

        //  A list is created to add the assigned menu_members.
        //  This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        getCurrentUserID().let { assignedUsersArrayList.add(it) } // adding the current user id.

        // Creating the instance of the Board and adding the values as per parameters.
        val boardId = System.currentTimeMillis().toString()
        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList,
            boardId
        )

        FirebaseDatabaseClass()
            .createBoard(this@CreateBoardActivity, board)
    }

    /**
     * A function for notifying the board is created successfully.
     */
    fun boardCreatedSuccessfully() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
}
