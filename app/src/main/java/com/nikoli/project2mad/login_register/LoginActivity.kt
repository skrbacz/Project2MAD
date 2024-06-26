package com.nikoli.project2mad.login_register

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nikoli.project2mad.MainActivity
import com.nikoli.project2mad.R

/**
 * Activity for user login.
 */
class LoginActivity : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()

    private var emailEDTV: EditText?= null
    private var passwordEDTV: EditText?= null
    private var loginBTN: Button?= null
    private var registerTV: TextView?= null
    private var cheat: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        cheat= findViewById(R.id.logoIV)

        emailEDTV= findViewById(R.id.emailEDTVL)
        passwordEDTV= findViewById(R.id.passwordEDTVL)
        loginBTN= findViewById(R.id.loginBTNL)
        registerTV= findViewById(R.id.registerTVL)
        cheat= findViewById(R.id.cheat)



        registerTV?.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


        loginBTN?.setOnClickListener {
            if(validLogin()){
                login()
                goToMain()
            }
        }

        // Simulate login with predefined credentials when logo is clicked
        cheat?.setOnClickListener {
            mAuth.signInWithEmailAndPassword("oliwia@oliwia.com", "!Qwerty123")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity,"You logged in successfully!", Toast.LENGTH_SHORT).show()
                        Log.d(ContentValues.TAG, "login with email: success")
                        goToMain()
                    } else {
                        Toast.makeText(this@LoginActivity,"Login failed! :${task.exception}", Toast.LENGTH_SHORT).show()
                        Log.w(ContentValues.TAG, "login with email: failure", task.exception)
                    }
                }
        }


    }

    /**
     * Validates the user input for login.
     *
     * @return true if the input is valid, false otherwise.
     */
    private fun validLogin(): Boolean {
        if(emailEDTV?.text.isNullOrBlank()){
            emailEDTV?.error = "Please enter your email"
            return false
        }

        val validEmail =
            android.util.Patterns.EMAIL_ADDRESS.matcher(emailEDTV?.text.toString()).matches()
        if (!validEmail) {
            emailEDTV?.error = "Please provide a valid email"
            return false
        }

        if(passwordEDTV?.text.isNullOrBlank()){
            passwordEDTV?.error = "Please provide your password"
            return false
        }
        return true

    }

    /**
     * Attempts to login with the provided credentials.
     */
    private fun login() {
        val email= emailEDTV?.text.toString().trim() { it <= ' ' }
        val password= passwordEDTV?.text.toString().trim() { it <= ' ' }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity,"You logged in successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(ContentValues.TAG, "login with email: success")
                } else {
                    Toast.makeText(this@LoginActivity,"Login failed! :${task.exception}", Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "login with email: failure", task.exception)
                }
            }
    }
    /**
     * Navigates to the main activity after successful login.
     */
    private fun goToMain(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}