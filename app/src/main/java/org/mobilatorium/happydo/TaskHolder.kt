package org.mobilatorium.happydo

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.custom_list_view_item.view.*

@RequiresApi(Build.VERSION_CODES.O)
class TaskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val action: CheckBox = itemView.action
    private val removeButton: Button = itemView.button_delete

    fun bind(task: Task) {
        action.text = task.action
        removeButton.setOnClickListener {
            val documentId:String = this.itemView?.tag.toString()
            MainActivity().db.collection("tasks")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener { Log.d("task_holder", "successfully removed!") }
                    .addOnFailureListener { e ->
                        Log.w("task_holder", "Error removing task", e)
                    }
            MainActivity().adapter.notifyDataSetChanged()
        }
    }

//    private fun deleteTask(){
//       
//    }
}
