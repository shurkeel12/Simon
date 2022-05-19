package com.example.simon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener { startGame()}
    }

    private fun startGame() {
        val intent = Intent()
        intent.setClass(this, SimonActivity::class.java)
        startActivity(intent)
    }
}