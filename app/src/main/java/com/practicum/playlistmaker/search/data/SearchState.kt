package com.practicum.playlistmaker.search.data

sealed class SearchState{
    object PrepareShowTracks : SearchState()
    object ShowEmptyResult : SearchState()
    object ShowTracksError : SearchState()
    object SearchTextClear : SearchState()
}
