package com.nikoli.project2mad

import GameAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nikoli.project2mad.db.GameData
import com.nikoli.project2mad.for_recycler_view.GameItem
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * History activity
 *
 * @constructor Create empty History activity
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var checkBoxGame1: CheckBox
    private lateinit var checkBoxGame2: CheckBox
    private lateinit var checkBoxGame3: CheckBox
    private lateinit var adapter: GameAdapter
    private var allGamesList = mutableListOf<GameItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val bottomNavigationView = findViewById<LinearLayout>(R.id.bottom_navigation)
        val buttonHistory = bottomNavigationView.findViewById<ImageView>(R.id.button_history)
        val buttonProfile = bottomNavigationView.findViewById<ImageView>(R.id.button_profile)
        val buttonHome = bottomNavigationView.findViewById<ImageView>(R.id.button_home)
        val clicked = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.main_pink_clicked))

        runBlocking {
            allGamesList= (fetchNumberSizeCongruency() + fetchStroopTest()+ fetchEriksensFlanker()).toMutableList()
        }

        buttonHistory.imageTintList = clicked

        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerView)
        checkBoxGame1 = findViewById(R.id.checkBox_game_one)
        checkBoxGame2 = findViewById(R.id.checkBox_game_two)
        checkBoxGame3 = findViewById(R.id.checkBox_game_three)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GameAdapter(allGamesList)
        recyclerView.adapter = adapter

        checkBoxGame1.setOnCheckedChangeListener { _, _ -> updateRecyclerView() }
        checkBoxGame2.setOnCheckedChangeListener { _, _ -> updateRecyclerView() }
        checkBoxGame3.setOnCheckedChangeListener { _, _ -> updateRecyclerView() }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        val filteredList = allGamesList.filter { game ->
            (checkBoxGame1.isChecked && game.name == "Stroop Test") ||
                    (checkBoxGame2.isChecked && game.name == "Eriksen's Flanker Test") ||
                    (checkBoxGame3.isChecked && game.name == "Number Size Congruency") ||
                    (!checkBoxGame1.isChecked && !checkBoxGame2.isChecked && !checkBoxGame3.isChecked)
        }.sortedByDescending { it.datePlayed }

        adapter.updateList(filteredList)
    }

    suspend fun fetchNumberSizeCongruency(): MutableList<GameItem> {
        return try {
        val querySnapshow = FirebaseFirestore.getInstance()
            .collection("numberSizeCongruency")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .collection("games")
            .get()
            .await()

        val gameList= mutableListOf<GameItem>()
        for (document in querySnapshow.documents){
            val gameData= document.toObject(GameData::class.java)
            if (gameData != null) {
                gameList.add(GameItem(gameData.name, gameData.reactionTime, gameData.accuracy, gameData.date))
            }
        }
        gameList
        }catch (e:Exception){
            mutableListOf<GameItem>()
        }
    }

    suspend fun fetchStroopTest(): MutableList<GameItem> {
        return try {
            val querySnapshow = FirebaseFirestore.getInstance()
                .collection("stroopTest")
                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .collection("games")
                .get()
                .await()

            val gameList= mutableListOf<GameItem>()
            for (document in querySnapshow.documents){
                val gameData= document.toObject(GameData::class.java)
                if (gameData != null) {
                    gameList.add(GameItem(gameData.name, gameData.reactionTime, gameData.accuracy, gameData.date))
                }
            }
            gameList
        }catch (e:Exception){
            mutableListOf<GameItem>()
        }
    }

    suspend fun fetchEriksensFlanker(): MutableList<GameItem> {
        return try {
            val querySnapshow = FirebaseFirestore.getInstance()
                .collection("eriksensFlankerTest")
                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .collection("games")
                .get()
                .await()

            val gameList= mutableListOf<GameItem>()
            for (document in querySnapshow.documents){
                val gameData= document.toObject(GameData::class.java)
                if (gameData != null) {
                    gameList.add(GameItem(gameData.name, gameData.reactionTime, gameData.accuracy, gameData.date))
                }
            }
            gameList
        }catch (e:Exception){
            mutableListOf<GameItem>()
        }
    }


}