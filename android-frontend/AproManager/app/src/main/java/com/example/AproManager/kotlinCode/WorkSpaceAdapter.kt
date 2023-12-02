package com.example.AproManager.kotlinCode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AproManager.R

class WorkSpaceAdapter(private val workspaces: List<Workspace> ) : RecyclerView.Adapter<WorkSpaceAdapter.WorkSpaceViewHolder>() {

    class WorkSpaceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            fun bind(workspace :Workspace) {
                val textViewWorkSpaceName: TextView =itemView.findViewById(R.id.textViewWorkspaceName)
                val recyclerViewBoards: RecyclerView =itemView.findViewById(R.id.recyclerViewBoards)
                textViewWorkSpaceName.text=workspace.name
                val boardAdapter = BoardAdapter(workspace.boards)
                recyclerViewBoards.adapter = boardAdapter
                recyclerViewBoards.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkSpaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workspace,parent,false)
        return WorkSpaceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return workspaces.size
    }

    override fun onBindViewHolder(holder: WorkSpaceViewHolder, position: Int) {
        val workspace=workspaces[position]
        holder.bind(workspace)
    }
}