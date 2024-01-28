package com.example.AproManager.kotlinCode.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R
import com.example.AproManager.databinding.ItemTaskBinding
import com.example.AproManager.kotlinCode.activities.TaskListActivity
import com.example.AproManager.kotlinCode.models.Task

open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<TaskListItemsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        binding.root.layoutParams = layoutParams
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    inner class MyViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            with(binding) {
                if (adapterPosition == list.size - 1) {
                    tvAddTaskList.visibility = View.VISIBLE
                    llTaskItem.visibility = View.GONE
                } else {
                    tvAddTaskList.visibility = View.GONE
                    llTaskItem.visibility = View.VISIBLE
                }

                tvTaskListTitle.text = task.title

                tvAddTaskList.setOnClickListener {
                    tvAddTaskList.visibility = View.GONE
                    cvAddTaskListName.visibility = View.VISIBLE
                }

                ibCloseListName.setOnClickListener {
                    tvAddTaskList.visibility = View.VISIBLE
                    cvAddTaskListName.visibility = View.GONE
                }

                ibDoneListName.setOnClickListener {
                    val listName = etTaskListName.text.toString()
                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.createTaskList(listName)
                        }
                    } else {
                        Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                    }
                }

                // Binding for editing list name
                ibEditListName.setOnClickListener {
                    etEditTaskListName.setText(task.title)
                    llTitleView.visibility = View.GONE
                    cvEditTaskListName.visibility = View.VISIBLE
                }

                ibCloseEditableView.setOnClickListener {
                    llTitleView.visibility = View.VISIBLE
                    cvEditTaskListName.visibility = View.GONE
                }

                ibDoneEditListName.setOnClickListener {
                    val listName = etEditTaskListName.text.toString()
                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.updateTaskList(adapterPosition, listName, task)
                        }
                    } else {
                        Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                    }
                }

                ibDeleteList.setOnClickListener {
                    alertDialogForDeleteList(adapterPosition, task.title)
                }

                tvAddCard.setOnClickListener {
                    tvAddCard.visibility = View.GONE
                    cvAddCard.visibility = View.VISIBLE

                    ibCloseCardName.setOnClickListener {
                        tvAddCard.visibility = View.VISIBLE
                        cvAddCard.visibility = View.GONE
                    }

                    ibDoneCardName.setOnClickListener {
                        val cardName = etCardName.text.toString()
                        if (cardName.isNotEmpty()) {
                            if (context is TaskListActivity) {
                                context.addCardToTaskList(adapterPosition, cardName)
                            }
                        } else {
                            Toast.makeText(context, "Please Enter Card Detail.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                rvCardList.layoutManager = LinearLayoutManager(context)
                rvCardList.setHasFixedSize(true)

                val adapter = CardListItemsAdapter(context, task.cards)
                rvCardList.adapter = adapter

                adapter.setOnClickListener(object :
                    CardListItemsAdapter.OnClickListener {
                    override fun onClick(cardPosition: Int) {
                        if (context is TaskListActivity) {
                            context.cardDetails(adapterPosition, cardPosition)
                        }
                    }
                })
            }
        }
    }

}

