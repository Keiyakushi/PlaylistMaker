package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<TrackResponse>

}