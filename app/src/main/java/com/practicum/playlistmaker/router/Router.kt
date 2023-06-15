package com.practicum.playlistmaker.router

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.player.activity.MediaActivity
import com.practicum.playlistmaker.search.activity.SearchActivity
import com.practicum.playlistmaker.search.data.Track

class Router() {
    fun addToMedia(track: Track, activity: AppCompatActivity) {
        val intent = Intent(activity, MediaActivity::class.java).apply {
            putExtra(SearchActivity.MEDIA_KEY, Gson().toJson(track))
        }
        activity.startActivity(intent)
    }

    fun getTrack(activity: AppCompatActivity): Track {
        return Gson().fromJson((activity.intent.getStringExtra(MediaActivity.MEDIA_KEY)),
            Track::class.java)
    }

}