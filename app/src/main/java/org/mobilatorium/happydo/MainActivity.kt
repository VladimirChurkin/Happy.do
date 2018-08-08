package org.mobilatorium.happydo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    // Создаем и инициализируем коллекцию для вывода тасков в лист вью
    private var tasks = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем наши таски в коллекцию
        getTasks(getToday())

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

        //создаем адаптер для данных, элемент лист вью - чекбокс
        val adapter = ArrayAdapter<String>(this, R.layout.list_view_item, tasks)
        list_view_tasks.adapter = adapter

        //собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        addTask.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            val editText = EditText(this)
            builder.setTitle("Добавление новой задачи")
            builder.setView(editText)

            builder.setPositiveButton("Добавить") { _, _ ->
                adapter.add(editText.text.toString())
                addNewTaskToDate(editText.text.toString(), getToday())
            }

            builder.setNegativeButton("Отмена") { _, _ -> }

            val dialog = builder.create()
            dialog.show()
        }
    }

    // Функция для получения текущей даты(так проще, и видно из всего кода)
    @SuppressLint("SimpleDateFormat")
    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
    }
}
