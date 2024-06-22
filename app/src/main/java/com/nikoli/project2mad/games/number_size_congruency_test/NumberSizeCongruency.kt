package com.nikoli.project2mad.games.number_size_congruency_test

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.nikoli.project2mad.MainActivity
import com.nikoli.project2mad.R
import com.nikoli.project2mad.db.GameData
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import kotlin.random.Random

/**
 * Number size congruency
 *
 * @constructor Create empty Number size congruency
 */
class NumberSizeCongruency : AppCompatActivity() {
    private lateinit var leftCircle: ImageView
    private lateinit var rightCircle: ImageView
    private lateinit var leftNumber: TextView
    private lateinit var rightNumber: TextView
    private lateinit var leftFrame: FrameLayout
    private lateinit var rightFrame: FrameLayout
    private lateinit var randomWordTV: TextView
    private  var randomWords: MutableList<String> = mutableListOf("BIG", "SMALL","ZERO","ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE")

    private var currentRound = 0
    private var startTime: Long = 0
    private val reactionTimes = mutableListOf<Long>()
    private var correctAnswers = 0
    private var secondPhaseStarted = false

    var db = Firebase.firestore 
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_size_congruency)

        leftCircle = findViewById(R.id.leftCircle)
        rightCircle = findViewById(R.id.rightCircle)
        leftNumber = findViewById(R.id.leftNumber)
        rightNumber = findViewById(R.id.rightNumber)
        leftFrame = findViewById(R.id.leftFrame)
        rightFrame = findViewById(R.id.rightFrame)

        randomWordTV= findViewById(R.id.randomWordTV)


        leftFrame.setOnClickListener { handleClick(isLeft = true) }
        rightFrame.setOnClickListener { handleClick(isLeft = false) }

        showRulesDialog()
    }

    private fun showRulesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Rules")
        builder.setMessage("In this phase you have to always click the bigger circle.")
        builder.setPositiveButton("Start") { dialog, _ ->
            dialog.dismiss()
            startGame()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun startGame() {
        currentRound = 0
        correctAnswers = 0
        reactionTimes.clear()
        startTime = SystemClock.elapsedRealtime()
        nextRound()
    }

    private fun nextRound() {
        if (currentRound == 8 && !secondPhaseStarted) {
            showRulesDialogSecondPhase()
            return
        } else if (currentRound >= 16) {
            endGame()
            return
        }
        if (currentRound in 4..7) {
            val randomWord = getRandomWord()
            randomWordTV.text = randomWord
            randomWordTV.visibility = TextView.VISIBLE
        } else {
            randomWordTV.visibility = TextView.GONE
        }

        val (leftSize, rightSize) = if (Random.nextBoolean()) {
            R.drawable.big_circle to R.drawable.small_circle
        } else {
            R.drawable.small_circle to R.drawable.big_circle
        }

        leftCircle.setImageResource(leftSize)
        rightCircle.setImageResource(rightSize)

        val leftNum = Random.nextInt(1, 10)
        var rightNum: Int
        do {
            rightNum = Random.nextInt(1, 10)
        } while (rightNum == leftNum)

        leftNumber.text = leftNum.toString()
        rightNumber.text = rightNum.toString()

        currentRound++
    }

    private fun getRandomWord(): String {
        return randomWords[Random.nextInt(randomWords.size)]
    }


    private fun showRulesDialogSecondPhase() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Rules")
        builder.setMessage("In this phase you always have to click the higher number.")
        builder.setPositiveButton("Start") { dialog, _ ->
            dialog.dismiss()
            secondPhaseStarted = true
            nextRound()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun handleClick(isLeft: Boolean) {
        val correct = if (currentRound <= 8) {
            (isLeft && leftCircle.drawable.constantState == resources.getDrawable(R.drawable.big_circle, null).constantState) ||
                    (!isLeft && rightCircle.drawable.constantState == resources.getDrawable(R.drawable.big_circle, null).constantState)
        } else {
            val leftNum = leftNumber.text.toString().toInt()
            val rightNum = rightNumber.text.toString().toInt()
            (isLeft && leftNum > rightNum) || (!isLeft && rightNum > leftNum)
        }

        if (correct) {
            correctAnswers++
        }

        reactionTimes.add(SystemClock.elapsedRealtime() - startTime)
        startTime = SystemClock.elapsedRealtime()

        nextRound()
    }

    private fun endGame() {
        val averageReactionTime = reactionTimes.average()
        val correctPercentage = (correctAnswers / 16.0) * 100
        Toast.makeText(this, "Game is over! Correct Answers: ${"%.2f".format(correctPercentage)}%, Avg Reaction Time: ${averageReactionTime}ms", Toast.LENGTH_LONG).show()
        runBlocking {
            sendData(correctPercentage)
        }

        goToMain()
    }

    private fun sendData(accuracy: Double) {
        val currentUser = mAuth.currentUser?.email
        if (currentUser == null) {
            Log.w("Firestore", "User not logged in")
            return
        }

        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentTime)

        val gameData = GameData("Number Size Congruency",formattedDate, accuracy, reactionTimes.average())
        Log.d("game data", gameData.toString())

        FirebaseFirestore.getInstance()
            .collection("numberSizeCongruency")
            .document(currentUser)
            .collection("games")
            .add(gameData)
            .addOnSuccessListener {
                Log.d("game data", "successfully added to firebase")
            }
            .addOnFailureListener { e ->
                Log.d("game data", "failure adding to firebase", e)
            }
    }


    private fun goToMain(){
        val intent = Intent(this@NumberSizeCongruency, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
