package com.example.AproManager.kotlinCode.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AproManager.R
import com.example.AproManager.databinding.ActivityCardDetailsBinding
import com.example.AproManager.kotlinCode.adapters.CardMemberListItemsAdapter
import com.example.AproManager.kotlinCode.adapters.CommentAdapter
import com.example.AproManager.kotlinCode.dialogs.LabelColorListDialog
import com.example.AproManager.kotlinCode.dialogs.MembersListDialog
import com.example.AproManager.kotlinCode.firebase.FirebaseDatabaseClass
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.Card
import com.example.AproManager.kotlinCode.models.Comments
import com.example.AproManager.kotlinCode.models.SelectedMembers
import com.example.AproManager.kotlinCode.models.Task
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CardDetailsActivity : BaseActivity(),CommentAdapter.OnClickListener
{

    private val mDatabase = FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/")

    private lateinit var binding: ActivityCardDetailsBinding
    // board details
    private lateinit var mBoardDetails: Board
    //  task item position
    private var mTaskListPosition: Int = -1
    // card item position
    private var mCardPosition: Int = -1
    // selected label color
    private var mSelectedColor: String = ""

    //  Assigned Members List.
    private lateinit var mMembersDetailList: ArrayList<User>
    //Due date
    private var mSelectedDueDateMilliSeconds:Long=0

    //comments
    private var mCommentList: ArrayList<Comments> = ArrayList()
    private lateinit var adapter:CommentAdapter
    private var boardId=""
    private var cardId=""
    private var taskId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupActionBar()

        binding.etNameCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)


        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        binding.tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

        setupSelectedMembersList()

        //if due date in database , set it
        mSelectedDueDateMilliSeconds=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate
        if(mSelectedDueDateMilliSeconds >0){
            val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate=simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            binding.tvSelectDueDate.text=selectedDate
        }


        binding.tvSelectMembers.setOnClickListener {
            membersListDialog()
        }
        binding.btnUpdateCardDetails
            .setOnClickListener {
                if (binding.etNameCardDetails.text.toString().isNotEmpty()) {
                    updateCardDetails()
                } else {
                    Toast.makeText(this@CardDetailsActivity, "Enter card name.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.tvSelectDueDate.setOnClickListener{
            showDatePicker()
        }

        //keep previous comments
        mCommentList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].commentList
        initRecyclerView()

        binding.postComment.setOnClickListener{
            postComment()
        }


         boardId= mBoardDetails.boardId
         listenForCommentUpdates(boardId,mTaskListPosition,mCardPosition) { updatedCommentList ->
            updateCommentListUI(updatedCommentList)
        }


    }


    private fun listenForCommentUpdates(boardId: String, taskId: Int, cardId: Int,callback: (ArrayList<Comments>) -> Unit) {
        val database =mDatabase
        val boardsRef = database.getReference("boards")
        val boardRef = boardsRef.child(boardId)
        val taskListRef = boardRef.child("taskList").child(taskId.toString())
        val cardRef = taskListRef.child("cards").child(cardId.toString())


        cardRef.child("commentList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updatedCommentList: ArrayList<Comments> = ArrayList()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(Comments::class.java) ?: continue
                    updatedCommentList.add(comment)
                }
                callback(updatedCommentList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun updateCommentListInDatabase() {
        FirebaseDatabaseClass().updateCommentListInDatabase( boardId,mTaskListPosition,mCardPosition,mCommentList)
    }

    private fun initRecyclerView() {
        binding.RecyclerviewComments.layoutManager = LinearLayoutManager(this)
        adapter = CommentAdapter(this, getCurrentUserID(), mCommentList, this, this)
        binding.RecyclerviewComments.adapter = adapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun postComment() {
        val commentText = binding.comment.text.toString()

        val sharedPrefs = this.getSharedPreferences(Constants.APROMANAGER_SHAREPREFERENCE, Context.MODE_PRIVATE)
        val profileUri=sharedPrefs.getString(Constants.profileUri, "") ?: ""
        if (commentText.isNotEmpty()) {
            val currentTime = Date().time
            val instanceOfComment = Comments(commentText, getUserName(), currentTime, profileUri)
            mCommentList.add(instanceOfComment)
            binding.comment.text.clear()
            adapter.notifyDataSetChanged()
           updateCommentListInDatabase()
        } else {
            Toast.makeText(this@CardDetailsActivity, "Enter a comment.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCardDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        binding.toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // A function to get all the data that is sent through intent.
    private fun getIntentData() {

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }


        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    /**
     * A function to get the result of add or updating the task list.
     */
    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        //finish()
    }

    /**
     * A function to update card details.
     */
    private fun updateCardDetails() {

        val card = Card(
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].cardId,
            binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        if (taskList.isNotEmpty() && taskList.last().cards.isEmpty()) {
            taskList.removeAt(taskList.size - 1)
        }

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirebaseDatabaseClass()
            .addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    /**
     * A function to show an alert dialog for the confirmation to delete the card.
     */
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to delete the card from the task list.
     */
    private fun deleteCard() {

        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirebaseDatabaseClass()
            .addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    /**
     * A function to remove the text and set the label color to the TextView.
     */
    private fun setColor() {
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    /**
     * A function to add some static label colors in the list.
     */
    private fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    /**
     * A function to launch the label color list dialog.
     */
    private fun labelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    /**
     * A function to launch and setup assigned members detail list into recyclerview.
     */
    private fun membersListDialog() {

        // updated assigned members list
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this@CardDetailsActivity,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {

                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()

            }
        }
        listDialog.show()
    }


    /**
     * A function to setup the recyclerView for card assigned members.
     */
    private fun setupSelectedMembersList() {

        // Assigned members of the Card.
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        // selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()


        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {

            // last item to show.
            selectedMembersList.add(SelectedMembers("", ""))

            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
            val adapter = CardMemberListItemsAdapter(this@CardDetailsActivity, selectedMembersList,true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(object :
                CardMemberListItemsAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }

    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            val sDayOfMonth = if (dayOfMonth < 10) "0${dayOfMonth}" else "$dayOfMonth"
            val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"
            val selectedDate = "${sDayOfMonth}/${sMonthOfYear}/${year}"
            binding.tvSelectDueDate.text = selectedDate

            val sdf=SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate=sdf.parse(selectedDate)
            mSelectedDueDateMilliSeconds=theDate!!.time
        }, year, month, day)

        dpd.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onLikeClick(position: Int, likeCountTextView: TextView,likeButton:ImageButton) {


        val comment = mCommentList[position]
        val userId = getCurrentUserID()

        if (!comment.dislikedBy.contains(userId) ) {
            if(comment.likedBy.contains(userId)) {
                comment.likedBy.remove(userId)
                likeButton.setImageResource(R.drawable.baseline_thumb_up_24)
            }else{
                comment.likedBy.add(userId)

                likeButton.setImageResource(R.drawable.thum_up_after_liked)
            }
        } else {
            //users already give dislike . do nothing
        }

        val likeCount = comment.likedBy.size
        if(likeCount>0)

            likeCountTextView.text = likeCount.toString()
        likeCountTextView.visibility = if (likeCount > 0) View.VISIBLE else View.INVISIBLE
        updateCommentListInDatabase()
        adapter.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDisLikeClick(position: Int, disLikeCountTextView: TextView,dislikeButton:ImageButton) {


        val comment = mCommentList[position]
        val userId = getCurrentUserID()

        if (!comment.likedBy.contains(userId) ) {
            if(comment.dislikedBy.contains(userId)) {
                comment.dislikedBy.remove(userId)
                dislikeButton.setImageResource(R.drawable.baseline_thumb_down_24)
            }else{
                comment.dislikedBy.add(userId)
                dislikeButton.setImageResource(R.drawable.baseline_thumb_down_after_dislike_24)
            }
        } else {
            //users already give like . do nothing
        }

        val dislikeCount = comment.dislikedBy.size
        disLikeCountTextView.text = dislikeCount.toString()
        disLikeCountTextView.visibility = if (dislikeCount > 0) View.VISIBLE else View.INVISIBLE
        updateCommentListInDatabase()
        adapter.notifyDataSetChanged()

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateCommentListUI(updatedCommentList: ArrayList<Comments>) {
        // Update the local comment list
        mCommentList.clear()
        mCommentList.addAll(updatedCommentList)

        // Notify the adapter that the dataset has changed
        adapter.notifyDataSetChanged()

        // Scroll the RecyclerView to the bottom to show the latest comment
        binding.RecyclerviewComments.scrollToPosition(adapter.itemCount - 1)

    }




}