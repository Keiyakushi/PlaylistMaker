package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.Track

const val HISTORY_KEY = "HISTORY_KEY"

class SearchHistory(val sharedPreferences: SharedPreferences) {

    fun saveHistory(historyList : ArrayList<Track>){
        val json = Gson().toJson(historyList)
        sharedPreferences.edit()
            .putString(HISTORY_KEY,json)
            .apply()
    }

    fun readHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }
}