package org.mobilatorium.happydo

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        //логинимся
        button_login.setOnClickListener {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }

        //переходим на сайт с инфой
        button_info.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://happy.do")))
        }
    }
}