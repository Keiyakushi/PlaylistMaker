package com.practicum.playlistmaker.search.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network {
    fun provideApi(): iTunesApi {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(iTunesApi::class.java)
    }
}