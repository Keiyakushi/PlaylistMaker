package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.playlist.data.Playlist

sealed class ResultStates(val result: List<Playlist>) {
    object Empty : ResultStates(result = emptyList())
    class HasPlaylists(result: List<Playlist>) : ResultStates(result = result)
}