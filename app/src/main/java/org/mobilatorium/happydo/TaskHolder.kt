package org.mobilatorium.happydo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import kotlinx.android.synthetic.main.custom_list_view_item.view.action


class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val action: CheckBox = itemView.action

    fun bind(task: Task) {
        action.text = task.action
    }
}
