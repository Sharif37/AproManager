package com.example.AproManager.kotlinCode.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.databinding.ItemCardNewBinding
import com.example.AproManager.kotlinCode.activities.TaskListActivity
import com.example.AproManager.kotlinCode.dialogs.MoveCardDialogFragment
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.Card
import com.example.AproManager.kotlinCode.models.SelectedMembers

open class CardListItemsAdapter(
    private val context: TaskListActivity,
    private val fragmentManager: FragmentManager,
    private var list: ArrayList<Card>,
    private var board: Board,
    val bindingTaskPosition: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding= ItemCardNewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.bind(model)
        }

    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    private fun showMoveCardDialog(taskPosition: Int, cardPosition: Int) {
        val moveCardDialog = MoveCardDialogFragment(context,board, taskPosition, cardPosition)
        moveCardDialog.show(fragmentManager, "MoveCardDialog")
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class MyViewHolder(private val binding: ItemCardNewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Card) {
            if (model.labelColor.isNotEmpty()) {
                binding.viewLabelColor.visibility = View.VISIBLE
                binding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                binding.viewLabelColor.visibility = View.GONE
            }

            binding.tvCardName.text = model.name

            if ((context as TaskListActivity)
                    .mAssignedMembersDetailList.size >0) {
                val selectedMembersList: ArrayList<SelectedMembers> =ArrayList()

                for(i in context.mAssignedMembersDetailList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMembersDetailList[i].id==j){
                            val selectedMembers=SelectedMembers(
                                context.mAssignedMembersDetailList[i].id,
                                context.mAssignedMembersDetailList[i].image

                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMembersList.size>0){
                    if(selectedMembersList.size==1 && selectedMembersList[0].id==model.createdBy){
                        binding.rvCardSelectedMembersList.visibility =View.GONE
                    }else
                    {
                        binding.rvCardSelectedMembersList.visibility =View.VISIBLE
                        binding.rvCardSelectedMembersList.layoutManager=
                            GridLayoutManager(context,4)
                        val adapter=CardMemberListItemsAdapter(context,selectedMembersList,false)
                        binding.rvCardSelectedMembersList.adapter=adapter

                        adapter.setOnClickListener(
                            object :CardMemberListItemsAdapter.OnClickListener{
                                override fun onClick() {
                                    if(onClickListener!=null){
                                        onClickListener!!.onClick(position)
                                    }

                                }

                        })


                    }
                }


            }else{
                binding.rvCardSelectedMembersList.visibility=View.GONE
            }

            binding.root.setOnClickListener {
                onClickListener?.onClick(adapterPosition)
            }


            binding.root.setOnLongClickListener {
                val cardPosition = bindingAdapterPosition
                if (cardPosition != RecyclerView.NO_POSITION) {
                    showMoveCardDialog(bindingTaskPosition,cardPosition)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }
        }
    }



}