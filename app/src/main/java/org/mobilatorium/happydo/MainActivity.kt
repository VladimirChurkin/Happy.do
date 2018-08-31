package org.mobilatorium.happydo

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Сначала присваеваем текущую дату, а затем как то работаем с ней(увеличиваем/уменьшаем)
    private var date = Calendar.getInstance()
    private var format = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupDateNavigation()
        setupAddNewTaskButton()

        // пишу авторизацию пока здесь, потому что пока только так умею
        
    }

    override fun onStart() {
        super.onStart()
        changeDate(date)
    }

    private fun setupDateNavigation() {

        // С помощью кнопки изменяем дату на один день вперед и смотрим таски на эту дату
        button_to_next_date.setOnClickListener {
            date.add(Calendar.DATE, +1)
            changeDate(date)
        }

        // С помощью кнопки изменяем дату на один день назад и смотрим таски на эту дату
        button_to_last_date.setOnClickListener {
            date.add(Calendar.DATE, -1)
            changeDate(date)
        }

        activity_main.setOnTouchListener(object: OnSwipeTouchListener(this){
            override fun onSwipeLeft() {
                date.add(Calendar.DATE, +1)
                changeDate(date)
            }

            override fun onSwipeRight() {
                date.add(Calendar.DATE, -1)
                changeDate(date)
            }
        })
    }

    private fun setupAddNewTaskButton() {

        // Собсна, создаем диалоговое окно и добавляем таски Лехиным методом)))
        button_add_task.setOnClickListener {
            val addNewTask = EditText(this)

            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Добавление новой задачи")
                    .setView(addNewTask)
                    .setPositiveButton("OK") { _, _ ->
                        addNewTaskToDate(addNewTask.text.toString().trim(), format.format(date.time))
                    }
                    .setNegativeButton("Отмена") { _, _ -> }
                    .create().show()

        }

    }

    private fun changeDate(newDate: Calendar) {
        date = newDate
        text_view_date.text = format.format(date.time)
        changeTasksRecyclerViewForDate(date)
    }

    private fun changeTasksRecyclerViewForDate(date: Calendar) {
        val query = db.collection("tasks").whereEqualTo("date", format.format(date.time))
        val options = FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task::class.java)
                .build()

        val adapter = object : FirestoreRecyclerAdapter<Task, TaskHolder>(options) {
            override fun onBindViewHolder(holder: TaskHolder, position: Int, task: Task) {
                holder.bind(task)

                task.id = snapshots.getSnapshot(position).id
                val docRef = db.collection("tasks").document(task.id)
                //удаляем таску
                holder.deleteButton.setOnClickListener {
                    removeTask(docRef)
                }
                //редактируем таску
                holder.editButton.setOnClickListener {
                   editTask(docRef, task.action)
                }
                //следим за состоянием чекбоксов
                holder.checkAction.isChecked = task.completed
                holder.action.setOnClickListener {
                    setChangeChecked(docRef, !task.completed)
                }
            }

            private fun setChangeChecked(docRef: DocumentReference, b: Boolean) {
                docRef.update("completed", b)
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

            private fun removeTask(docRef: DocumentReference) {
                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Удаление задачи")
                        .setMessage("Вы действительно хотите удалить задачу?")
                        .setPositiveButton("OK"){_,_ ->
                            docRef.delete()
                        }
                        .setNegativeButton("Отмена"){_,_ ->}
                        .create().show()
            }

            private fun editTask(docRef: DocumentReference, name: String) {
                val editTaskAction = EditText(this@MainActivity)
                editTaskAction.setText(name)

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Редактирование задачи")
                        .setView(editTaskAction)
                        .setPositiveButton("OK"){_,_ ->
                            docRef.update("action", editTaskAction.text.toString().trim())
                        }
                        .setNegativeButton("Отмена"){_,_ ->}
                        .create().show()
            }
        }
        adapter.startListening()

        recycler_view_tasks.adapter = adapter
        recycler_view_tasks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun addNewTaskToDate(task: String, date: String) {
        //добавляем в Firebase таску. ID документа - имя таски
        db.collection("tasks").document()
                .set(hashMapOf("date" to date, "completed" to false, "action" to task).toMap())
                .addOnSuccessListener { Log.d("main_activity", "successfully added!") }
                .addOnFailureListener { e ->
                    Log.w("main_activity", "Error adding new task", e)
                }

    }
}
