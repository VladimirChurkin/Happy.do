package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

// собсна, вот наш класс, с ним и работай, если хочешь что то изменить или метод добавить
// и да, все методы теперь public, т.к их не видит MainActivity, когда они приватные
class TaskFirebase {
    @SuppressLint("SetTextI18n")
    fun getTasks(date: String) :String{
        val db = FirebaseFirestore.getInstance()
        var result = "1223"
        db.collection("tasks")
                .whereEqualTo("date", date)
                .addSnapshotListener(EventListener { snapshot, e ->
                    if (e != null) {
                        Log.w("main_activity", "Listen failed.", e)
                        return@EventListener
                    }
                     result = "$date \n ${snapshot?.documents?.map { "${it.get("action")} ${it.get("completed")} \n" }}"
                })
        return result
    }

//    fun printTasksFromDateToLogcat(date: String) {
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("tasks")
//                .whereEqualTo("date", date)
//                .addSnapshotListener(EventListener { snapshot, e ->
//                    if (e != null) {
//                        Log.w("main_activity", "Listen failed.", e)
//                        return@EventListener
//                    }
//                    Log.d("main_activity", "tasks for $date: ${snapshot?.documents?.map { it.data }}")
//                })
//    }

    fun addNewTaskToDate(task: String, date: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("tasks").document()
                .set(hashMapOf("date" to date, "completed" to false, "action" to task).toMap())
                .addOnSuccessListener { Log.d("main_activity", "successfully added!") }
                .addOnFailureListener { e ->
                    Log.w("main_activity", "Error adding new task", e)
                }
    }
}