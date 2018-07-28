package org.mobilatorium.happydo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("days").document("2018-07-28")
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    Log.d("main_activity", "DocumentSnapshot data: " + document.data)
                } else {
                    Log.d("main_activity", "No such document")
                }
            } else {
                Log.d("main_activity", "get failed with ", task.exception)
            }
        }
    }
}
