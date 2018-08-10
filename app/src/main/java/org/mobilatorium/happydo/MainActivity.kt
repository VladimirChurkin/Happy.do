package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val tasks = ArrayList<Task>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_view_tasks.adapter = TaskAdapter(this, tasks)

        // Получаем наши таски в коллекцию
        getTasks(getToday())
        TextDate.text = getToday()


        addNewTasksThroughAlertDialog()

    }

    @SuppressLint("SetTextI18n")
    private fun getTasks(date: String) {
        db.collection("tasks")
                .whereEqualTo("date", date)
                .addSnapshotListener(EventListener { snapshot, e ->
                    if (e != null) {
                        Log.w("main_activity", "Listen failed.", e)
                        return@EventListener
                    }
                    textView.text = "$date \n ${snapshot?.documents?.map { "${it.get("action")} ${it.get("completed")} \n" }}"
                    Log.d("main_activity", "tasks for $date: ${snapshot?.documents?.map { it.data }}")

                })
    }

    private fun addNewTaskToDate(task: String, date: String) {
        db.collection("tasks").document()
                .set(hashMapOf("date" to date, "completed" to false, "action" to task).toMap())
                .addOnSuccessListener { Log.d("main_activity", "successfully added!") }
                .addOnFailureListener { e ->
                    Log.w("main_activity", "Error adding new task", e)
                }
    }

    private fun addNewTasksThroughAlertDialog() {

        //собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        addTask.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            val addNewTask = EditText(this)
            builder.setTitle("Добавление новой задачи")
                    .setView(addNewTask)
                    .setPositiveButton("OK"){_,_->
                        TaskAdapter(this, tasks).add(Task(addNewTask.text.toString(), false))
                        addNewTaskToDate(addNewTask.text.toString(), getToday())
                    }
                    .setNegativeButton("Отмена"){_,_->}
                    .create().show()
        }
    }

    // Функция для получения текущей даты(так проще, и видно из всего кода)
    @SuppressLint("SimpleDateFormat")
    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }
}
