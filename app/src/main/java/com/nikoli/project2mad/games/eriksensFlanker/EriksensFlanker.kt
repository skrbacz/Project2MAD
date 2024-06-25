package com.nikoli.project2mad.games.eriksensFlanker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nikoli.project2mad.MainActivity
import com.nikoli.project2mad.R
import com.nikoli.project2mad.db.GameData
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random

/**
 * Eriksens flanker
 *
 * @constructor Create empty Eriksens flanker
 */
class EriksensFlanker : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var firstArrow: ImageView
    private lateinit var secondArrow: ImageView
    private lateinit var thirdArrow: ImageView
    private lateinit var fourthArrow: ImageView
    private lateinit var fifthArrow: ImageView
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button
    private lateinit var random: Random
    private var isRightArrow: Boolean = false
    private var correctCount: Int = 0
    private var startTimeMillis: Long = 0
    private var endTimeMillis: Long = 0
    private var gameEnded: Boolean = false
    private var reactionStartTimeMillis: Long = 0
    private var totalReactionTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eriksens)

        firstArrow = findViewById(R.id.arrow_first)
        secondArrow = findViewById(R.id.arrow_second)
        thirdArrow = findViewById(R.id.arrow_third)
        fourthArrow = findViewById(R.id.arrow_fourth)
        fifthArrow = findViewById(R.id.arrow_fifth)
        buttonLeft = findViewById(R.id.buttonLeft)
        buttonRight = findViewById(R.id.buttonRight)

        random = Random()
        showRulesDialog()
    }

    private fun showRulesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Rules")
        builder.setMessage(
            "Click the left button when the central arrow points to the left.\n" +
                    "Click the right button when the central arrow points to the right."
        )
        builder.setPositiveButton("Start") { dialog, _ ->
            dialog.dismiss()
            startGame()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun startGame() {
        setupGame()
        startTimeMillis = System.currentTimeMillis()
    }

    private fun setupGame() {
        if (gameEnded) {
            return // If game has ended, do not continue setting up
        }

        if (correctCount == 7) {
            endGame()
            return
        }

        // Randomly determine if center arrow points right (true) or left (false)
        isRightArrow = random.nextBoolean()

        // Set each arrow direction randomly
        firstArrow.setImageResource(if (random.nextBoolean()) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left)
        secondArrow.setImageResource(if (random.nextBoolean()) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left)
        thirdArrow.setImageResource(if (isRightArrow) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left)
        fourthArrow.setImageResource(if (random.nextBoolean()) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left)
        fifthArrow.setImageResource(if (random.nextBoolean()) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left)

        // Record the time when the arrows are displayed
        reactionStartTimeMillis = System.currentTimeMillis()

        buttonLeft.setOnClickListener {
            checkAnswer(false)
        }

        buttonRight.setOnClickListener {
            checkAnswer(true)
        }
    }

    private fun checkAnswer(clickedRight: Boolean) {
        if (gameEnded) {
            return
        }

        if (clickedRight == isRightArrow) {
            correctCount++
            val reactionEndTimeMillis = System.currentTimeMillis()
            val reactionTime = reactionEndTimeMillis - reactionStartTimeMillis
            totalReactionTime += reactionTime

            if (correctCount == 7) {
                endGame()
            } else {
                setupGame()
            }
        } else {
            Toast.makeText(this, "Wrong button!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun endGame() {
        endTimeMillis = System.currentTimeMillis()

        val elapsedSeconds = (endTimeMillis - startTimeMillis) / 1000.0
        val accuracy = (correctCount / 7.0) * 100
        val averageReactionTime = totalReactionTime / 7.0

        Toast.makeText(
            this,
            "Accuracy: %.2f%%\nAverage Reaction Time: %.2f ms".format(accuracy, averageReactionTime),
            Toast.LENGTH_LONG
        ).show()

        gameEnded = true

        runBlocking {
            sendData(accuracy)
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

        val gameData = GameData("Eriksen's Flanker Test", formattedDate, accuracy, totalReactionTime.toDouble() / 7)
        Log.d("game data", gameData.toString())

        FirebaseFirestore.getInstance()
            .collection("eriksensFlankerTest")
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

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
