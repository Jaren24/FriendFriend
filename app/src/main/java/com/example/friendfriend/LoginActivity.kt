package com.example.friendfriend
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.register_activity.*
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        bt_login.setOnClickListener{
            val email = edt_emails.text.toString()
            val password = edt_LoginPassword.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!,password)
                .addOnCompleteListener {
                    login()
                     }
                .addOnFailureListener {
                    Toast.makeText(this, "Please re-enter your ID and Password", Toast.LENGTH_SHORT).show()
                }
        }
        DontHave.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun login(){
        val intents = Intent(this, ChatRoom::class.java)
        intents.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intents)
    }

}