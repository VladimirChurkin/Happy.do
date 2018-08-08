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
    //вот это вот и есть объект нашего класса с методами Firebase
    private val firebase = TaskFirebase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list_view_tasks.adapter = TaskAdapter(this, tasks)

        // Получаем наши таски в коллекцию
        // Чутка переделал, не хочет из другого левого класса работать с текст вью. Теперь метод getTasks возвращает строку
        // с тасками
        textView.text=firebase.getTasks(getToday())

        addNewTasksThroughAlertDialog()

    }

    private fun addNewTasksThroughAlertDialog() {
        //собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        addTask.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            val editTextAddNewTask = EditText(this)
            builder.setTitle("Добавление новой задачи")
            builder.setView(editTextAddNewTask)

            builder.setPositiveButton("Добавить") { _, _ ->
                TaskAdapter(this,tasks).add(Task(editTextAddNewTask.text.toString(), false))
                firebase.addNewTaskToDate(editTextAddNewTask.text.toString(), getToday())
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
