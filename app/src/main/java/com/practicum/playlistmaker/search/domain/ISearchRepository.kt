package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.Track

interface ISearchRepository{
    fun loadTracks(query:String, onSuccess : (ArrayList<Track>) -> Unit, onError : () -> Unit)
}