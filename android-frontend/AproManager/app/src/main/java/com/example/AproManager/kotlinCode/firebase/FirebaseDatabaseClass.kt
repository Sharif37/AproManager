package com.example.AproManager.kotlinCode.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.AproManager.kotlinCode.activities.CardDetailsActivity
import com.example.AproManager.kotlinCode.activities.CreateBoardActivity
import com.example.AproManager.kotlinCode.activities.MainActivity
import com.example.AproManager.kotlinCode.activities.MembersActivity
import com.example.AproManager.kotlinCode.activities.MyProfileActivity
import com.example.AproManager.javaCode.Activities.SignInActivity
import com.example.AproManager.javaCode.Activities.SignUpActivity
import com.example.AproManager.kotlinCode.activities.TaskListActivity
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.Comments
import com.example.AproManager.kotlinCode.models.User
import com.example.AproManager.kotlinCode.utils.Constants
import com.example.AproManager.kotlinCode.utils.Constants.APROMANAGER_SHAREPREFERENCE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FirebaseDatabaseClass {

    private val mDatabase =
        FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/")

    /**
     * A function to make an entry of the registered user in the firebase database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mDatabase.reference.child(Constants.USERS)
            .child(getCurrentUserID())
            .setValue(userInfo)
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error creating user", e)
            }
    }

    fun checkEmailExists(context: Context,email: String) {

        val query = mDatabase.reference.child("users").orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isExistingUser = dataSnapshot.exists()
                //println(isExistingUser)

                if (isExistingUser) {
                    // Show a message to the user
                    Constants.showToast(context, "This email is already registered.")
                } else {
                    // Email is not registered
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Constants.showToast(context, "Error checking email")
            }
        })

    }


    /**
     * A function to SignIn using firebase and get the user details from Firebase Database.
     */
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        val userReference = mDatabase.reference.child(Constants.USERS).child(getCurrentUserID())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val loggedInUser = dataSnapshot.getValue(User::class.java) ?: return
                saveCaches(activity, loggedInUser.name, loggedInUser.image)

                when (activity) {
                    is SignInActivity -> activity.signInSuccess(loggedInUser)
                    is MainActivity -> activity.updateNavigationUserDetails(
                        loggedInUser,
                        readBoardsList
                    )

                    is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }

                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }

                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                }

            }
        })
    }

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        val userReference = mDatabase.reference.child(Constants.USERS).child(getCurrentUserID())

        userReference.updateChildren(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                when (activity) {
                    is MainActivity -> activity.tokenUpdateSuccess()
                    is MyProfileActivity -> activity.profileUpdateSuccess()
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is MainActivity -> activity.hideProgressDialog()
                    is MyProfileActivity -> activity.hideProgressDialog()
                }


            }
    }

    /**
     * A function for creating a board and making an entry in the database.
     */
    fun createBoard(activity: CreateBoardActivity, board: Board) {
        val boardReference = mDatabase.reference.child(Constants.BOARDS).push()

        boardReference.setValue(board)
            .addOnSuccessListener {
                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()

            }
    }


    /**
     * A function to get the list of created boards from the database.
     */
    fun getBoardsList(activity: MainActivity) {
        val boardsReference = mDatabase.reference.child(Constants.BOARDS)

        boardsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val boardsList: ArrayList<Board> = ArrayList()

                for (boardSnapshot in dataSnapshot.children) {
                    val board = boardSnapshot.getValue(Board::class.java) ?: continue
                    board.boardId = boardSnapshot.key ?: ""
                    if (board.assignedTo.contains(getCurrentUserID())) {
                        boardsList.add(board)
                    }

                }

                activity.populateBoardsListToUI(boardsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.hideProgressDialog()
            }
        })
    }


    /**
     * A function to get the Board Details.
     */
    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        val boardReference = mDatabase.reference.child(Constants.BOARDS).child(documentId)

        boardReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val board = dataSnapshot.getValue(Board::class.java) ?: return
                board.boardId = dataSnapshot.key ?: ""

                activity.boardDetails(board)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.hideProgressDialog()
            }
        })
    }


    /**
     * A function to create a task list in the board detail.
     */

    fun addUpdateTaskList(activity: Activity, board: Board) {

        val reference =
            mDatabase.getReference(Constants.BOARDS).child(board.boardId).child(Constants.TASK_LIST)

        reference.setValue(board.taskList)
            .addOnSuccessListener {

                if (activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                } else if (activity is CardDetailsActivity) {
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener { e ->
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                } else if (activity is CardDetailsActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while updating task list.", e)
            }

        // Add a ValueEventListener to receive real-time updates
        /*reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Handle changes in the task list data
                val updatedTaskList: ArrayList<Task> = ArrayList()
                val updatedCommentList:ArrayList<Comments> = ArrayList()

                for (taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java) ?: continue
                    updatedTaskList.add(task)

                    // Now, for each task, you can iterate over its cards
                    for (cardSnapshot in taskSnapshot.child("cards").children) {
                        val card = cardSnapshot.getValue(Card::class.java) ?: continue

                        // Now, for each card, you can iterate over its comments
                        for (commentSnapshot in cardSnapshot.child("").
                        child("commentList").children) {
                            val comment = commentSnapshot.getValue(Comments::class.java) ?: continue
                            updatedCommentList.add(comment)
                        }
                    }
                }

                // Update the UI with the new task list data
                if (activity is TaskListActivity) {
                    activity.updateTaskListUI(updatedTaskList)
                } else if (activity is CardDetailsActivity) {
                    activity.updateCommentListUI(updatedCommentList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })*/
    }

    /**
     * A function to assign a updated members list to board.
     */
    fun getAssignedMembersListDetails(activity: Activity, assignedTo: List<String>) {
        val usersReference = mDatabase.reference.child(Constants.USERS)

        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersList: ArrayList<User> = ArrayList()

                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java) ?: continue
                    if (assignedTo.contains(user.id)) {
                        usersList.add(user)
                    }
                }

                when (activity) {
                    is MembersActivity -> activity.setupMembersList(usersList)
                    is TaskListActivity -> activity.boardMembersDetailList(usersList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (activity is MembersActivity) {
                    activity.hideProgressDialog()
                } else if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }
            }
        })
    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        val usersReference = mDatabase.reference.child(Constants.USERS)

        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java) ?: continue
                    if (user.email == email) {
                        activity.memberDetails(user)
                        return
                    }
                }
                activity.hideProgressDialog()
                activity.showErrorSnackBar("No such member found.")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity.hideProgressDialog()
            }
        })
    }

    /**
     * A function to get the list of user details which is assigned to the board.
     */
    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        val boardReference = mDatabase.reference.child(Constants.BOARDS).child(board.boardId)

        val updatedAssignedTo = board.assignedTo.toMutableList()
        updatedAssignedTo.add(user.id)

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = updatedAssignedTo

        boardReference.updateChildren(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()

            }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser


        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    private fun saveCaches(activity: Activity, userName: String, uri: String) {
        val sharedPrefs =
            activity.getSharedPreferences(APROMANAGER_SHAREPREFERENCE, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(Constants.User_Name, userName)
        editor.putString(Constants.profileUri, uri)
        editor.apply()
    }


    fun updateCommentListInDatabase(
        boardId: String,
        taskId: Int,
        cardId: Int,
        commentList: ArrayList<Comments>
    ) {
        val database = mDatabase
        val boardsRef = database.getReference("boards")
        val boardRef = boardsRef.child(boardId)
        val taskListRef = boardRef.child("taskList").child(taskId.toString())
        val cardRef = taskListRef.child("cards").child(cardId.toString())
        cardRef.child("commentList").setValue(commentList)
            .addOnSuccessListener {
                Log.d("Firebase", "Comment list updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error updating comment list: $e")
            }
    }


}