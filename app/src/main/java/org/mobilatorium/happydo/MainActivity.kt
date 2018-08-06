package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
<<<<<<< HEAD
import kotlinx.android.synthetic.main.activity_main.*
=======
import kotlinx.android.synthetic.main.activity_main.addFeedCatToDoButton




>>>>>>> db4ac793dfcb0bc215cbaad7e3c365c32b034285


class MainActivity : AppCompatActivity() {

    val randomTasks = arrayListOf("купить кефир", "позвонить Маме", "покормить кота", "заработать $10К")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printTasksFromDateToLogcat("2018-12-31")

        addFeedCatToDoButton.setOnClickListener { _ ->
            addNewTaskToDate(randomTasks.shuffled()[0], "2018-12-31")
        }
<<<<<<< HEAD

        getTasks("2018-12-31")

    }

    @SuppressLint("SetTextI18n")
    private fun getTasks(date: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("tasks")
                .whereEqualTo("date", date)
                .addSnapshotListener(EventListener { snapshot, e ->
                    if (e != null) {
                        Log.w("main_activity", "Listen failed.", e)
                        return@EventListener
                    }
                    textView.text = "$date \n ${snapshot?.documents?.map { "${it.get("action")} ${it.get("completed")} \n" }}"
                })
=======
>>>>>>> db4ac793dfcb0bc215cbaad7e3c365c32b034285
    }

    private fun printTasksFromDateToLogcat(date: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("tasks")
                .whereEqualTo("date", date)
                .addSnapshotListener(EventListener { snapshot, e ->
                    if (e != null) {
                        Log.w("main_activity", "Listen failed.", e)
                        return@EventListener
                    }
                    Log.d("main_activity", "tasks for $date: ${snapshot?.documents?.map { it.data }}")
                })
    }

    private fun addNewTaskToDate(task: String, date: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("tasks").document()
                .set(hashMapOf("date" to date, "completed" to false, "action" to task).toMap())
                .addOnSuccessListener { Log.d("main_activity", "successfully added!") }
                .addOnFailureListener { e -> Log.w("main_activity", "Error adding new task", e) }
    }
}
