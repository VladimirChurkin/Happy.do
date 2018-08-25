package org.mobilatorium.happydo

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Сначала присваеваем текущую дату, а затем как то работаем с ней(увеличиваем/уменьшаем)
    private var date = LocalDate.now()
    private var format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupDateNavigation()
        setupAddNewTaskButton()
    }

    override fun onStart() {
        super.onStart()
        changeDate(date)
    }

    private fun setupDateNavigation() {

        // С помощью кнопки изменяем дату на один день вперед и смотрим таски на эту дату
        button_to_next_date.setOnClickListener {
            changeDate(date.plusDays(1))
        }

        // С помощью кнопки изменяем дату на один день назад и смотрим таски на эту дату
        button_to_last_date.setOnClickListener {
            changeDate(date.minusDays(1))
        }

    }

    private fun setupAddNewTaskButton() {

        // Собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        button_add_task.setOnClickListener {
            val addNewTask = EditText(this)

            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Добавление новой задачи")
                    .setView(addNewTask)
                    .setPositiveButton("OK") { _, _ ->
                        addNewTaskToDate(addNewTask.text.toString(), date.format(format))
                    }
                    .setNegativeButton("Отмена") { _, _ -> }
                    .create().show()

        }

    }

    private fun changeDate(newDate: LocalDate) {
        date = newDate
        text_view_date.text = date.format(format)
        changeTasksRecyclerViewForDate(date)
    }

    private fun changeTasksRecyclerViewForDate(date: LocalDate) {
        val query = db.collection("tasks").whereEqualTo("date", date.format(format))
        val options = FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task::class.java)
                .build()

        val adapter = object : FirestoreRecyclerAdapter<Task, TaskHolder>(options) {
            override fun onBindViewHolder(holder: TaskHolder, position: Int, task: Task) {
                holder.bind(task)
                //удаляем таску
                holder.deleteButton.setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                            .setTitle("Удаление задачи")
                            .setMessage("Вы действительно хотите удалить задачу?")
                            .setPositiveButton("OK"){_,_ ->
                                db.collection("tasks")
                                        .document(holder.action.text.toString())
                                        .delete()
                            }
                            .setNegativeButton("Отмена"){_,_ ->}
                            .create().show()
                }
                //редактируем таску
                holder.editButton.setOnClickListener {
                    val editTaskAction = EditText(this@MainActivity)
                    editTaskAction.setText(task.action)

                    AlertDialog.Builder(this@MainActivity)
                            .setTitle("Редактирование задачи")
                            .setView(editTaskAction)
                            .setPositiveButton("OK"){_,_ ->
                                db.collection("tasks")
                                        .document(holder.action.text.toString())
                                        .update("action", editTaskAction.text.toString())
                            }
                            .setNegativeButton("Отмена"){_,_ ->}
                            .create().show()
                }
                //следим за состоянием чекбоксов
                holder.action.isChecked = task.completed
                //смотрим, нажат чекбокс или нет, и меняем его значение с нажатого на ненажатый

                holder.action.setOnClickListener {
                    db.collection("tasks")
                            .document(holder.action.text.toString())
                            .update("completed", !holder.action.isChecked)
                }
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): TaskHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view = LayoutInflater.from(group.context)
                        .inflate(R.layout.custom_list_view_item, group, false)

                return TaskHolder(view)
            }

            override fun onDataChanged() {
                Log.i("MainActivity", "onDataChanged")
                if(this.itemCount == 0){
                    text_view.text = "Нажмите кнопку \"Добавить задачу\", чтобы создать заметку"
                }
                else text_view.text = ""
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.w("MainActivity", e)
            }
        }
        adapter.startListening()

        recycler_view_tasks.adapter = adapter
        recycler_view_tasks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun addNewTaskToDate(task: String, date: String) {
        //добавляем в Firebase таску. ID документа - имя таски
        db.collection("tasks").document(task)
                .set(hashMapOf("date" to date, "completed" to false, "action" to task).toMap())
                .addOnSuccessListener { Log.d("main_activity", "successfully added!") }
                .addOnFailureListener { e ->
                    Log.w("main_activity", "Error adding new task", e)
                }

    }
}
