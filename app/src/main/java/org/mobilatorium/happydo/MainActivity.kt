package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val tasks = ArrayList<Task>()
<<<<<<< HEAD
    //вот это вот и есть объект нашего класса с методами Firebase
    val firebase = TaskFirebase()
=======
>>>>>>> parent of 2543e2e... putting firebase methods in separated class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_view_tasks.adapter = TaskAdapter(this, tasks)

        // Получаем наши таски в коллекцию
<<<<<<< HEAD
        // Чутка переделал, не хочет из другого левого класса работать с текст вью. Теперь метод getTasks возвращает строку
        // с тасками
        firebase.getTasks(getToday())
        textView.text = firebase.result
=======
        getTasks(getToday())

>>>>>>> parent of 2543e2e... putting firebase methods in separated class
        addNewTasksThroughAlertDialog()

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
    }

//    private fun printTasksFromDateToLogcat(date: String) {
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

    private fun addNewTaskToDate(task: String, date: String) {
        val db = FirebaseFirestore.getInstance()

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
            val editTextAddNewTask = EditText(this)
            builder.setTitle("Добавление новой задачи")
            builder.setView(editTextAddNewTask)

            builder.setPositiveButton("Добавить") { _, _ ->
<<<<<<< HEAD
                TaskAdapter(this,tasks).add(Task(editTextAddNewTask.text.toString(), false))
<<<<<<< HEAD
                firebase.addNewTaskToDate(editTextAddNewTask.text.toString(), getToday())
=======
                addNewTaskToDate(editTextAddNewTask.text.toString(), getToday())
>>>>>>> parent of f2d4f13... small change
=======
                TaskAdapter(this, tasks).add(Task(editTextAddNewTask.text.toString(), false))
                addNewTaskToDate(editTextAddNewTask.text.toString(), getToday())
>>>>>>> parent of 2543e2e... putting firebase methods in separated class
            }

            builder.setNegativeButton("Отмена") { _, _ -> }

            builder.create().show()
        }
    }

    // Функция для получения текущей даты(так проще, и видно из всего кода)
    @SuppressLint("SimpleDateFormat")
    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }
}
