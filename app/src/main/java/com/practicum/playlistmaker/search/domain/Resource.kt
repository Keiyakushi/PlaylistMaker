package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.Track

sealed class Resource(val data: ArrayList<Track>? = null, val error: NetworkError? = null) {
    class Success(data: ArrayList<Track>) : Resource(data = data)
    class Error(error: NetworkError) : Resource(error = error)
}
