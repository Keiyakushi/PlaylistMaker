package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.playlist.data.Playlist

sealed class ResultStatesBottomSheet(val result: Playlist) {
    object Empty : ResultStatesBottomSheet(result = Playlist(1, "", "", "", emptyList(), 0))
    class AlreadyHas(result: Playlist) : ResultStatesBottomSheet(result)
    class NotHas(result: Playlist) : ResultStatesBottomSheet(result)
}
