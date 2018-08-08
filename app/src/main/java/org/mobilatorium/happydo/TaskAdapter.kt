package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.TextView
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.CheckBox


@SuppressLint("ParcelCreator")
class TaskAdapter(context: Context, private val tasks:ArrayList<Task>): ArrayAdapter<Task>(context, 0, tasks), Parcelable {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem: View? = convertView
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.custom_list_view_item, parent, false)

        val currentTask = tasks[position]

        val action = listItem!!.findViewById(R.id.action) as CheckBox
        action.text = currentTask.action

        val redactButton = listItem!!.findViewById(R.id.redact_button) as Button

        val deleteButton = listItem!!.findViewById(R.id.delete_button) as TextView

        return listItem
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}