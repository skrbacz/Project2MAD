package com.nikoli.project2mad

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nikoli.project2mad.games.number_size_congruency_test.NumberSizeCongruency
import com.nikoli.project2mad.games.stroop.StroopTest
import com.nikoli.project2mad.games.eriksensFlanker.EriksensFlanker

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {

    private var gameOneBtn: Button?= null
    private var gameTwoBtn: Button?= null
    private var gameThreeBtn: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<LinearLayout>(R.id.bottom_navigation)

        val buttonHistory = bottomNavigationView.findViewById<ImageView>(R.id.button_history)
        val buttonProfile= bottomNavigationView.findViewById<ImageView>(R.id.button_profile)
        val buttonHome = bottomNavigationView.findViewById<ImageView>(R.id.button_home)
        val clicked = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.main_pink_clicked))

        buttonHome.imageTintList = clicked

        buttonHistory.setOnClickListener {
            val intent= Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        buttonProfile.setOnClickListener {
            val intent= Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }


        gameOneBtn= findViewById(R.id.gameOneBTN)
        gameTwoBtn= findViewById(R.id.gameTwoBTN)
        gameThreeBtn= findViewById(R.id.gameThreeBTN)

        gameOneBtn?.setOnClickListener{
            val intent= Intent(this, StroopTest::class.java)
            startActivity(intent)
        }

        gameTwoBtn?.setOnClickListener {
            val intent= Intent(this, EriksensFlanker::class.java)
            startActivity(intent)
        }

        gameThreeBtn?.setOnClickListener {
            val intent= Intent(this, NumberSizeCongruency::class.java)
            startActivity(intent)
        }


    }
}