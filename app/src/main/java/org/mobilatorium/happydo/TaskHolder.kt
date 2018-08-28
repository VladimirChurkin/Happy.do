package org.mobilatorium.happydo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.custom_list_view_item.view.*


class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val action: TextView = itemView.action
    val checkAction: CheckBox = itemView.check_action
    val deleteButton:Button = itemView.button_delete
    val editButton:Button = itemView.button_edit

    fun bind(task: Task) {
        action.text = task.action
    }
}
