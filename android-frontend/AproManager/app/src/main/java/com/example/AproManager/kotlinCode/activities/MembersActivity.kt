package com.example.AproManager.kotlinCode.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivityMembersBinding
import com.example.AproManager.databinding.DialogSearchMemberBinding
import com.example.AproManager.kotlinCode.adapters.MemberListItemsAdapter
import com.example.AproManager.kotlinCode.firebase.FirestoreClass
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    // A global variable for Board Details.
    private lateinit var mBoardDetails: Board

    // A global variable for Assigned Members List.
    private lateinit var mAssignedMembersList: ArrayList<User>

    // A global variable for notifying any changes done or not in the assigned members list.
    private var anyChangesDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )
    }

    override fun onBackPressed() {
        if (anyChangesDone) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * A function to setup assigned members list into recyclerview.
     */
    fun setupMembersList(list: ArrayList<User>) {

        mAssignedMembersList = list

        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this@MembersActivity)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        binding.rvMembersList.adapter = adapter
    }

    /**
     * Method is used to show the Custom Dialog.
     */
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/

        val dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener(View.OnClickListener {

            val email = dialogBinding.etEmailSearchMember.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()

                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass()
                    .getMemberDetails(this@MembersActivity, email)
            } else {
                showErrorSnackBar("Please enter members email address.")
                /*Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        })
        dialogBinding.tvCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        //Start the dialog and display it on screen.
        dialog.show()
    }

    fun memberDetails(user: User) {

        mBoardDetails.assignedTo.add(user.id)

        FirestoreClass()
            .assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
    }

    /**
     * A function to get the result of assigning the members.
     */
    fun memberAssignSuccess(user: User) {

        hideProgressDialog()

        mAssignedMembersList.add(user)

        anyChangesDone = true

        setupMembersList(mAssignedMembersList)
        SendNotificationToUserAsyncTask(
            mBoardDetails.name,user.fcmToken).execute()
    }

    private inner class SendNotificationToUserAsyncTask(val boardName:String,val token:String): AsyncTask<Any, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }
        override fun doInBackground(vararg params: Any?): String {
            var result : String
            var connection:HttpURLConnection? =null
            try {
                val url= URL(Constants.FCM_BASE_URL)
                connection=url.openConnection() as HttpURLConnection
                connection.doOutput=true
                connection.doInput=true
                connection.instanceFollowRedirects=false
                connection.requestMethod="POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")
                connection.setRequestProperty(Constants.FCM_AUTHORIZATION,"${Constants.FCM_KEY}=${Constants.SERVER_KEY}")
                connection.useCaches=false
                val wr=DataOutputStream(connection.outputStream)
                val jsonRequest=JSONObject()
                val dataObject=JSONObject()
                dataObject.put(Constants.KEY_TITLE,"Assigned to the board $boardName")
                dataObject.put(Constants.KEY_MESSAGE,"You have been Assigned to the Board"
                        + "by ${mAssignedMembersList[0].name}")
                jsonRequest.put(Constants.KEY_DATA,dataObject)
                jsonRequest.put(Constants.Key_TO,token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult:Int=connection.responseCode
                if(httpResult==HttpURLConnection.HTTP_OK){
                    val inputStream=connection.inputStream
                    val reader=BufferedReader(InputStreamReader(inputStream))
                    val sb=StringBuilder()
                    var line:String?
                    try{
                        while (reader.readLine().also{line=it}!=null){
                            sb.append(line+"\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result=sb.toString()
                }else{
                    result=connection.responseMessage
                }


            }catch (e:SocketTimeoutException){
                result ="Connection Timeout"
            }catch (e:Exception){
                result="Error :"+e.message
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
            Log.e("Json Response Result",result!!)

        }

    }
}