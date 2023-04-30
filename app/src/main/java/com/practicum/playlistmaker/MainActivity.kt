package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.search)
        val button2 = findViewById<Button>(R.id.media)
        val button3 = findViewById<Button>(R.id.setting)
        button.setOnClickListener{
         val searchIntent = Intent(this,SearchActivity::class.java)
         startActivity(searchIntent)
        }
        button2.setOnClickListener{
            val settingsIntent = Intent(this,MediaStore::class.java)
            startActivity(settingsIntent)
        }
        button3.setOnClickListener{
            val settingsIntent = Intent(this,SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}