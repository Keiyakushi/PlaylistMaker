package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.Track

interface ISearchInteractor {
    fun loadTracks(query:String, onSuccess : (ArrayList<Track>) -> Unit, onError : () -> Unit)
}