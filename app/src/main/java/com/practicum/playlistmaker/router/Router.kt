package com.practicum.playlistmaker.router

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.player.activity.MediaActivity
import com.practicum.playlistmaker.search.activity.SearchActivity

class Router(private val activity: AppCompatActivity) {
    fun addToMedia(track: Track) {
        val intent = Intent(activity, MediaActivity::class.java).apply {
            putExtra(SearchActivity.MEDIA_KEY, Gson().toJson(track))
        }
        activity.startActivity(intent)
    }

    fun getTrack(): Track {
        return Gson().fromJson((activity.intent.getStringExtra(MediaActivity.MEDIA_KEY)),
            Track::class.java)
    }

}