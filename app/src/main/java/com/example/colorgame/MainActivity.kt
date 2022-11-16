package com.example.colorgame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.colorgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sqliteHelper: SQLiteHelper

    private val currentPlayer = CurrentPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var name = intent.getStringExtra("name").toString()
        if (name == "null") name = ""
        binding.nameInput.setText(name)

        sqliteHelper = SQLiteHelper.getInstance(this)

        //sqliteHelper.deleteData()

        binding.goBtn.setOnClickListener {
            if (binding.nameInput.text.toString().isNotEmpty() && binding.nameInput.text.toString().isNotBlank()) {
                addPlayer()
                val intent = Intent(this, GameProcessActivity::class.java)
                currentPlayer.name = binding.nameInput.text.toString()
                intent.putExtra("currentPlayer", currentPlayer)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.nameInput.afterTextChanged {
            if (it.contains("\n")) {
                if (it.length == 1) {
                    binding.nameInput.text?.clear()
                } else {
                    binding.nameInput.setText(it.takeWhile { it1 -> it1 != '\n' })
                }
                hideKeyboard()
            }
        }

        binding.nameInput.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
            }
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun addPlayer() {
        val name = binding.nameInput.text.toString()

        val plr = PlayerModel(name = name)
        sqliteHelper.insertPlayer(plr)
        getPlayers()
    }

    private fun getPlayers() {
        val plrList = sqliteHelper.getAllPlayers()
        Log.e("AllPlayers", plrList.toString())
    }
}