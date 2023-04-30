package com.practicum.playlistmaker.player.presenter

import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.player.MediaActivity

class Router(private val activity: AppCompatActivity) {
    fun getUrl() : Track{
        return Gson().fromJson((activity.intent.getStringExtra(MediaActivity.MEDIA_KEY)), Track::class.java)
    }
}