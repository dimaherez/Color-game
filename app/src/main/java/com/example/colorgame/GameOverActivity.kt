package com.example.colorgame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.colorgame.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameOverBinding

    private lateinit var sqliteHelper: SQLiteHelper

    private lateinit var currentPlayer: CurrentPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sqliteHelper = SQLiteHelper.getInstance(this)

        currentPlayer = intent.getSerializableExtra("currentPlayer") as CurrentPlayer

        val player = sqliteHelper.getByName(currentPlayer.name.toString())


        binding.finalScore.text = currentPlayer.currentScore.toString()
        binding.best.text = getString(R.string.best, player.best)

        if (currentPlayer.currentScore > player.best) {
            binding.finalScoreHeading.text = getString(R.string.new_best)
            sqliteHelper.updateBestByName(currentPlayer.name.toString(), currentPlayer.currentScore)
        }


        binding.tryAgainBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("name", currentPlayer.name)
            startActivity(intent)
        }
    }
}