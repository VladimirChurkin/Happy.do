package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*


@SuppressLint("ParcelCreator")
class TaskAdapter(context: Context, private var tasks:ArrayList<Task>): ArrayAdapter<Task>(context, 0, tasks), Parcelable {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem: View? = convertView
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.custom_list_view_item, parent, false)

        val currentTask = tasks[position]

        val action = listItem!!.findViewById(R.id.action) as CheckBox
        action.text = currentTask.action
        //установка чекбокса нажатым в зависимости от значения, переданного в конструктор Task
        // при добавлении таски
        action.isChecked = currentTask.completed


        // редактируем таски
        val redactButton = listItem!!.findViewById(R.id.redact_button) as Button
        redactButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val redactTask = EditText(context)
            redactTask.setText(currentTask.action)
            builder.setTitle("Редактирование задачи")
                    .setView(redactTask)
                    .setPositiveButton("OK"){_,_->
                        tasks.set(position, Task(redactTask.text.toString(), false))
                        this.notifyDataSetChanged()
                    }
                    .setNegativeButton("Отмена"){_,_->}
                    .create().show()
        }

        // удаляем таски
        val deleteButton = listItem!!.findViewById(R.id.delete_button) as TextView
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Удаление задачи")
                    .setMessage("Вы действительно хотите удалить задачу?")
                    .setPositiveButton("OK"){_,_->
                        tasks.remove(currentTask)
                        this.notifyDataSetChanged()
                    }
                    .setNegativeButton("Отмена"){_,_->}
                    .create().show()
        }

        return listItem
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}