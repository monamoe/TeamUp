package app.helloteam.sportsbuddyapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import app.helloteam.sportsbuddyapp.views.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordRest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_rest)

        findViewById<TextView>(R.id.passSubmitButton).setOnClickListener {//go to forgot password activity
            val emailAddress = findViewById<EditText>(R.id.emailAddressInput).text.toString()
            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("Log", "Email sent.")
                        Toast.makeText(this, "Password reset successfully sent", Toast.LENGTH_LONG).show()
                    }
                }


            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            }

        }


}