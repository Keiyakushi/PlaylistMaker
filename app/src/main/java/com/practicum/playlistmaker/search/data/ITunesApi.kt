package com.practicum.playlistmaker.search.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String): Response<TrackResponse>?
}