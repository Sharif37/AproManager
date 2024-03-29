package com.example.AproManager.kotlinCode.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.utils.Constants
import com.example.AproManager.kotlinCode.utils.Constants.APROMANAGER_SHAREPREFERENCE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

open class BaseActivity: AppCompatActivity() {
    private var doubleBackToExitPressedOnce=false
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    fun showProgressDialog(text :String){
        mProgressDialog= Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        val processText=mProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        processText.text=text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }


    fun getUserName(): String {
        val sharedPrefs = this.getSharedPreferences(APROMANAGER_SHAREPREFERENCE, Context.MODE_PRIVATE)
        return sharedPrefs.getString(Constants.User_Name, "") ?: ""
    }
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.snackbar_error_color
            )
        )
        snackBar.show()
    }



}