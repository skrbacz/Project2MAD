package com.nikoli.project2mad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nikoli.project2mad.games.moca.MoCA
import com.nikoli.project2mad.games.ravlt.RAVLT
import com.nikoli.project2mad.games.tmt.TMT

class MainActivity : AppCompatActivity() {

    private var gameOneBtn: Button?= null
    private var gameTwoBtn: Button?= null
    private var gameThreeBtn: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameOneBtn= findViewById(R.id.gameOneBTN)
        gameTwoBtn= findViewById(R.id.gameTwoBTN)
        gameThreeBtn= findViewById(R.id.gameThreeBTN)

        gameOneBtn?.setOnClickListener{
            val intent= Intent(this, MoCA::class.java)
            startActivity(intent)
        }

        gameTwoBtn?.setOnClickListener {
            val intent= Intent(this, TMT::class.java)
            startActivity(intent)
        }

        gameThreeBtn?.setOnClickListener {
            val intent= Intent(this, RAVLT::class.java)
            startActivity(intent)
        }


    }
}