package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.db.entity.AppDatabase

const val HISTORY_KEY = "HISTORY_KEY"

class SearchHistory(val sharedPreferences: SharedPreferences, val gson: Gson) {

    fun saveHistory(historyList: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(HISTORY_KEY, gson.toJson(historyList))
            .apply()
    }

    fun readHistory(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray()
        return gson.fromJson(json, Array<Track>::class.java)
    }

}