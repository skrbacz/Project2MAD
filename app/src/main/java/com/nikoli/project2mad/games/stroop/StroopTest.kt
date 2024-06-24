package com.nikoli.project2mad.games.stroop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
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

class StroopTest : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    private var reactionTime: Double=0.0

    private lateinit var textView: TextView
    private lateinit var frameLayout: FrameLayout
    private val colors = arrayOf("red", "blue", "green", "yellow", "black", "purple", "orange", "brown", "pink")
    private val colorValues = intArrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.rgb(255, 225, 26), Color.BLACK, Color.rgb(136,6,206), Color.rgb(255, 150, 0), Color.rgb(139, 69, 19), Color.rgb(255, 192, 203))
    private val random = Random()
    private var round = 0
    private var correctResponses = 0
    private var totalResponses = 0
    private var matchingRounds = 0
    private var matchingResponses = 0
    private var startTime: Long = 0
    private var totalReactionTime: Long = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stroop_test)

        textView = findViewById(R.id.textView3)
        frameLayout = findViewById(R.id.frameLayout)

        frameLayout.setOnClickListener {
            checkResponse(true)
        }

        showRulesDialog()
    }

    private fun showRulesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Rules")
        builder.setMessage("In this phase, a color name will appear on the screen. If the name of the color and the color in which it is written match, click on the text. If they do not match, do not click.")
        builder.setPositiveButton("Start") { dialog, _ ->
            dialog.dismiss()
            startNewRound()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun startNewRound() {
        if (round >= 16) {
            showResults()
            return
        }

        val colorName = colors[random.nextInt(colors.size)]
        var colorValue = colorValues[random.nextInt(colorValues.size)]
        val isMatching = random.nextInt(100) < 45

        if (isMatching) {
            colorValue = getColorValueByName(colorName)
            matchingRounds++
        }

        textView.text = colorName
        textView.setTextColor(colorValue)

        round++
        startTime = System.currentTimeMillis()

        handler.postDelayed({
            checkResponse(false)
        }, 3000)
    }

    private fun getColorValueByName(colorName: String): Int {
        return when (colorName) {
            "red" -> Color.RED
            "blue" -> Color.BLUE
            "green" -> Color.GREEN
            "yellow" -> Color.rgb(255, 225, 26)
            "black" -> Color.BLACK
            "purple" -> Color.rgb(136,6,206)
            "orange" -> Color.rgb(255, 150, 0) // more orange
            "brown" -> Color.rgb(139, 69, 19)
            "pink" -> Color.rgb(255, 192, 203)
            else -> Color.BLACK
        }
    }

    private fun checkResponse(userClicked: Boolean) {
        val reactionTime = System.currentTimeMillis() - startTime
        val isMatching = textView.currentTextColor == getColorValueByName(textView.text.toString())

        if (userClicked && isMatching) {
            correctResponses++
            totalReactionTime += reactionTime
            matchingResponses++
        } else if (!userClicked && !isMatching) {
            correctResponses++
        }

        totalResponses++
        handler.removeCallbacksAndMessages(null)
        startNewRound()
    }

    private fun showResults() {
        val accuracy = correctResponses.toDouble() / totalResponses * 100
        val averageReactionTime = if (matchingResponses > 0) totalReactionTime.toDouble() / matchingResponses else 0.0
        reactionTime=averageReactionTime

        runBlocking {
            sendData(accuracy)
        }
        Toast.makeText(this, "Accuracy: %.2f%%\nAverage Reaction Time: %.2f ms".format(accuracy, averageReactionTime), Toast.LENGTH_LONG).show()
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

        val gameData = GameData("Stroop Test",formattedDate, accuracy, reactionTime)
        Log.d("game data", gameData.toString())

        FirebaseFirestore.getInstance()
            .collection("stroopTest")
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
        val intent = Intent(this@StroopTest, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
