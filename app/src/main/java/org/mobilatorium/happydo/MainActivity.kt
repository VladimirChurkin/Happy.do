package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val tasks = ArrayList<Task>()
    private val db = FirebaseFirestore.getInstance()

    //сначала присваеваем текущую дату, а затем как то работаем с ней(увеличиваем/уменьшаем)
    private var date = LocalDate.now();
    private var format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_view_tasks.adapter = TaskAdapter(this, tasks)

        // Получаем наши таски в коллекцию
        getTasks(date.format(format))
        TextDate.text = date.format(format)

        addNewTasksThroughAlertDialog()

        toNextDay()
        toLastDay()
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
                        addNewTaskToDate(addNewTask.text.toString(), date.format(format))
                    }
                    .setNegativeButton("Отмена"){_,_->}
                    .create().show()
        }
    }

    private fun toNextDay(){
        //с помощью кнопки изменяем дату на один день вперед и смотрим таски на эту дату
        toNextDate.setOnClickListener {
            date = date.plusDays(1)
            getTasks(date.format(format))
            TextDate.text = date.format(format)
        }
    }

    private fun toLastDay(){
        //с помощью кнопки изменяем дату на один день назад и смотрим таски на эту дату
        toLastDate.setOnClickListener {
            date = date.minusDays(1)
            getTasks(date.format(format))
            TextDate.text = date.format(format)
        }
    }
}
