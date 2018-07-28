package org.mobilatorium.happydo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.addFeedCatToDoButton






class MainActivity : AppCompatActivity() {

    val randomTasks = arrayListOf("купить кефир", "позвонить Маме", "покормить кота", "заработать $10К")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printTasksFromDateToLogcat("2018-12-31")

        addFeedCatToDoButton.setOnClickListener { _ ->
            addNewTaskToDate(randomTasks.shuffled()[0], "2018-12-31")
        }
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
