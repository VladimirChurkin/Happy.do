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

    // Сначала присваеваем текущую дату, а затем как то работаем с ней(увеличиваем/уменьшаем)
    private var date = LocalDate.now()
    private var format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_view_tasks.adapter = TaskAdapter(this, tasks)

        // Получаем наши таски в коллекцию
        getTasks(date.format(format))
        text_view_date.text = date.format(format)

        addNewTasksThroughAlertDialog()

        changeDay()

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

                    if ("${snapshot?.documents?.map { it["action"] } }" != "[]") {
                        text_view.text = "${snapshot?.documents?.map { "${it["action"]} ${it["completed"]} \n" }}"
                        text_view.textSize = 15f
                        text_view.gravity = 2
                    } else {
                        text_view.text = "Для добавления заметок нажмите на кнопку \"Добавить заметку\""
                        text_view.textSize = 30f
                        text_view.gravity = 1
                    }

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

        // Собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        button_add_task.setOnClickListener {

            val builder = AlertDialog.Builder(this@MainActivity)
            val addNewTask = EditText(this)

            builder.setTitle("Добавление новой задачи")
                    .setView(addNewTask)
                    .setPositiveButton("OK") { _, _ ->
                        TaskAdapter(this, tasks).add(Task(addNewTask.text.toString(), false))
                        addNewTaskToDate(addNewTask.text.toString(), date.format(format))
                    }
                    .setNegativeButton("Отмена") { _, _ -> }
                    .create().show()

        }

    }

    private fun reloadingDate() {
        getTasks(date.format(format))
        text_view_date.text = date.format(format)
    }

    private fun changeDay() {

        // С помощью кнопки изменяем дату на один день вперед и смотрим таски на эту дату
        button_to_next_date.setOnClickListener {
            date = date.plusDays(1)
            reloadingDate()
        }

        // С помощью кнопки изменяем дату на один день назад и смотрим таски на эту дату
        button_to_last_date.setOnClickListener {
            date = date.minusDays(1)
            reloadingDate()
        }

    }

}
