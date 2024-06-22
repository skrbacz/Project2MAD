package com.nikoli.project2mad

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nikoli.project2mad.db.UserData
import com.nikoli.project2mad.login_register.LoginActivity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

/**
 * Profile activity
 *
 * @constructor Create empty Profile activity
 */
class ProfileActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var firstNameTV: TextView
    private lateinit var sexTV: TextView
    private lateinit var ageTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var logOutTV: TextView
    private lateinit var user: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bottomNavigationView = findViewById<LinearLayout>(R.id.bottom_navigation)

        val buttonHistory = bottomNavigationView.findViewById<ImageView>(R.id.button_history)
        val buttonProfile= bottomNavigationView.findViewById<ImageView>(R.id.button_profile)
        val buttonHome = bottomNavigationView.findViewById<ImageView>(R.id.button_home)
        val clicked = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.main_pink_clicked))

        firstNameTV= findViewById(R.id.firstNameTV)
        sexTV= findViewById(R.id.sexTV)
        ageTV= findViewById(R.id.ageTV)
        emailTV= findViewById(R.id.emailTV)
        logOutTV= findViewById(R.id.logOutTV)


        runBlocking {
            user = fetchUserData()!!
            firstNameTV.text = user.name
            sexTV.text = "Sex: ${user.sex}"
            ageTV.text = "Age: ${user.age}"
            emailTV.text = "Email: ${mAuth.currentUser?.email.toString()}"
        }


        buttonProfile.imageTintList = clicked

        buttonHistory.setOnClickListener{
            val intent= Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        buttonHome.setOnClickListener {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        logOutTV.setOnClickListener {
            mAuth.signOut()
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    suspend fun fetchUserData(): UserData? {
        val docRef = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
        val userData = docRef.get().await().toObject(UserData::class.java)
        return userData
    }
}