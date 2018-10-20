package org.mobilatorium.happydo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*
<<<<<<< HEAD
=======
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*


>>>>>>> feature/45-realize_autorization

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        //логинимся
        button_login.setOnClickListener {
<<<<<<< HEAD
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }

        //переходим на сайт с инфой
        button_info.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://happy.do")))
=======
            val providers = Arrays.asList(
                    AuthUI.IdpConfig.EmailBuilder().build())

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().getCurrentUser()
                startActivity(Intent(this, MainActivity::class.java))
                // ...
            } else {
            }
>>>>>>> feature/45-realize_autorization
        }
    }
}