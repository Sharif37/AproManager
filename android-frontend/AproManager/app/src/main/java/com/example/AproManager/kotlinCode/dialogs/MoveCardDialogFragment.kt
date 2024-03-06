package com.example.AproManager.kotlinCode.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.AproManager.R
import com.example.AproManager.kotlinCode.activities.TaskListActivity
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.utils.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MoveCardDialogFragment(
    val activity: TaskListActivity,
    val board: Board, private val taskPosition: Int, private val cardPosition: Int) : DialogFragment() {
    val list=board.taskList

    private lateinit var listSpinner: Spinner
    private lateinit var positionSpinner: Spinner
    private val mDatabase = FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/")


    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var positionAdapter: ArrayAdapter<Int>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.move_card_dialog_layout, null)

            // Initialize UI components
            listSpinner = view.findViewById(R.id.boardSpinner)!!
            positionSpinner = view.findViewById(R.id.positionSpinner)!!


            // Initialize board adapter with task list titles
            val listTitles = list.map { it.title }.dropLast(1)
            listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listTitles)
            listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            listSpinner.adapter = listAdapter


            val selectedListIndex = listSpinner.selectedItemPosition
            updatePositionAdapter(selectedListIndex)

            // Set listener for board spinner selection
            listSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updatePositionAdapter(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No action needed
                }
            }

            builder.setView(view)
                .setPositiveButton("Move") { dialog, id ->

                    if (selectedListIndex != AdapterView.INVALID_POSITION ) {

                        val destinationTaskListPosition = listSpinner.selectedItemPosition
                        val destinationCardPosition = positionSpinner.selectedItemPosition
                        moveCard(board, taskPosition, cardPosition, destinationTaskListPosition, destinationCardPosition)
                    } else {
                        Toast.makeText(requireContext(), "Please select a list and a card", Toast.LENGTH_SHORT).show()
                    }
                }

                .setNegativeButton(R.string.cancel) { dialog, id ->
                    getDialog()?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updatePositionAdapter(boardIndex: Int) {

        val selectedList = list[boardIndex]
        val positions = (1..selectedList.cards.size).toList()
        positionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, positions)
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = positionAdapter

    }

    private fun moveCard(board: Board, sourceTaskListPosition: Int, sourceCardPosition: Int, destinationTaskListPosition: Int, destinationCardPosition: Int) {

        val database = mDatabase
        val boardsRef = database.getReference("boards")
        val boardRef = boardsRef.child(board.boardId)

       boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mBoard = dataSnapshot.getValue(Board::class.java) ?: return
                val taskList=mBoard.taskList

                // Retrieve the source and destination task lists
                val sourceTaskList = taskList[sourceTaskListPosition]
                val destinationTaskList = taskList[destinationTaskListPosition]
               // Log.d("kaj_hoice?","testcase: $destinationTaskList")

                // Retrieve the card to be moved
                val cardToMove = sourceTaskList.cards[sourceCardPosition]

                // Remove the card from its source position
                sourceTaskList.cards.removeAt(sourceCardPosition)

                // Insert the card into the destination position
                if (destinationCardPosition >= destinationTaskList.cards.size) {
                    destinationTaskList.cards.add(cardToMove)
                } else {
                    if(destinationCardPosition==-1)
                    {
                        destinationTaskList.cards.add(0, cardToMove)
                    }else
                    {
                        destinationTaskList.cards.add(destinationCardPosition, cardToMove)
                    }

                }

                // Update the task list in the database
                mDatabase.getReference(Constants.BOARDS).child(board.boardId).child(Constants.TASK_LIST)
                    .setValue(taskList)
                    .addOnSuccessListener {
                        activity.updateTaskListUI(taskList)

                    }
                    .addOnFailureListener {
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                }
        })
    }


}


