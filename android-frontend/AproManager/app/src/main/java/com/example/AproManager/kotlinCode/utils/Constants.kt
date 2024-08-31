package com.example.AproManager.kotlinCode.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass

object Constants {




    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"
    const val User_Name: String = "user_name"
    const val profileUri:String="Profile_Uri"

    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"
    const val COMMENT_LIST: String="commentList"

    // Firebase database field names
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"

    const val ID: String = "id"
    const val EMAIL: String = "email"

    const val BOARD_DETAIL: String = "board_detail"

    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val BOARD_MEMBERS_LIST: String = "board_members_list"

    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"


    //share preference
    const val APROMANAGER_SHAREPREFERENCE="aproManagerPref"
    const val Fcm_Token_Updated="fcmTokenUpdated"
    const val Fcm_Token="fcmToken"

    const val FCM_BASE_URL:String="https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String= "authorization"
    const val FCM_KEY:String="key"
    const val SERVER_KEY:String = "AAAAqbuk6bk:APA91bE4fQikr_ij0XjJQW5gmA2G1WpqEqpzX8nh74EudswKaot14u13x32u1pKSn2305I9Ne1Zd_lYNRiQYDl9j3QdZefFQ9mXT5pjOau2i50_cUptMXcljmSw__dgavFpjBBvG8eb4"
    const val KEY_TITLE:String= "title"
    const val KEY_MESSAGE:String = "message"
    const val KEY_DATA:String = "data"
    const val Key_TO:String="to"
    const val Image_URL="IMAGE_URI"
    const val DESCRIPTION="Description"

    // FCM token key
    const val FCM_TOKEN = "fcm_token"

    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showToast(context: Context , message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}