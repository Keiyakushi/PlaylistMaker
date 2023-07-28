package com.practicum.playlistmaker.router

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.data.db.entity.AppDatabase
import com.practicum.playlistmaker.data.db.entity.TrackEntity
import com.practicum.playlistmaker.player.activity.MediaPlayerActivity
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.Resource
import com.practicum.playlistmaker.ui.root.search.SearchFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Router(val gson: Gson,) {
    fun addToMedia(track: Track, activity: AppCompatActivity) {
        val intent = Intent(activity, MediaPlayerActivity::class.java).apply {
            putExtra(SearchFragment.MEDIA_KEY, gson.toJson(track))
        }
        activity.startActivity(intent)
    }

    fun getTrack(activity: AppCompatActivity): Track {
        return gson.fromJson((activity.intent.getStringExtra(MediaPlayerActivity.MEDIA_KEY)),
            Track::class.java)
    }

}