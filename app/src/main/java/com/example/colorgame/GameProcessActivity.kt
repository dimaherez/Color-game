package com.example.colorgame

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.colorgame.databinding.ActivityGameProcessBinding
import java.io.Serializable
import java.util.*

class GameProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameProcessBinding
    private val colors: Map<String, String> = mapOf(
        "yellow" to "#FFFF00",
        "red" to "#FF0000",
        "blue" to "#0000FF",
        "purple" to "#A020F0",
        "orange" to "#FFA500",
        "pink" to "#ffc0cb",
        "green" to "#00FF00",
        "brown" to "#964B00",
        "white" to "#FFFFFF",
        "cyan" to "#00FFFF"
    )
    private var score: Int = 0
    private lateinit var correctBox: TextView
    private lateinit var boxes: List<TextView>
    private var timer:Timer = Timer()

    private lateinit var currentPlayer: CurrentPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameProcessBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        currentPlayer = intent.getSerializableExtra("currentPlayer") as CurrentPlayer

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        binding.box1.setOnClickListener { box1Click() }
        binding.box2.setOnClickListener { box2Click() }
        binding.box3.setOnClickListener { box3Click() }
        binding.box4.setOnClickListener { box4Click() }

        boxes = listOf(binding.box1, binding.box2, binding.box3, binding.box4)

        processGame()
    }

    private fun processGame() {
        setTimer()

        binding.score.text = score.toString()

        val colorsCopy: MutableMap<String, String> = colors.toMutableMap()

        val correct = getRandomColor(colorsCopy)

        binding.colorText.text = getRandomColor(colorsCopy).key;
        binding.colorText.setTextColor(Color.parseColor(correct.value))


        val twoRandomBoxes = boxes.asSequence().shuffled().take(2).toList()
        correctBox = twoRandomBoxes.first()
        correctBox.text = correct.key

        twoRandomBoxes.last().text = binding.colorText.text

        boxes.forEach {
            if (it != correctBox && it != twoRandomBoxes.last())
                it.text = getRandomColor(colorsCopy).key
        }
    }

    private fun setTimer() {
        timer.cancel()
        timer.purge()
        timer = Timer()
        var timerCount = binding.timerProgressBar.max
        val period:Long = binding.timerProgressBar.max.toLong()

        timer.schedule(object:TimerTask() {
            override fun run() {
                timerCount--
                binding.timerProgressBar.progress = timerCount

                if (timerCount == 0) {
                    gameOver()
                }
            }
        }, 0, period)
    }

    private fun getRandomColor(map: MutableMap<String, String>): Map.Entry<String, String> {
        val element = map.map { it.key to it.value }.shuffled().toMap().entries.elementAt(0);
        map.remove(element.key)
        return element;
    }

    private fun gameOver() {
        timer.cancel()
        timer.purge()
        val intent = Intent(this, GameOverActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        currentPlayer.currentScore = score
        intent.putExtra("currentPlayer", currentPlayer)
        startActivity(intent)
    }

    private fun box1Click() {
        if (correctBox == binding.box1) {
            score++
            processGame()
        } else {
            gameOver()
        }
    }

    private fun box2Click() {
        if (correctBox == binding.box2) {
            score++;
            processGame()
        } else {
            gameOver()
        }
    }

    private fun box3Click() {
        if (correctBox == binding.box3) {
            score++;
            processGame()
        } else {
            gameOver()
        }
    }

    private fun box4Click() {
        if (correctBox == binding.box4) {
            score++;
            processGame()
        } else {
            gameOver()
        }
    }
}

