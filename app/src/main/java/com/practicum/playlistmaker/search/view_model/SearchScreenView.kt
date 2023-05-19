package com.practicum.playlistmaker.search.view_model

import com.practicum.playlistmaker.search.data.Track

interface SearchScreenView {
    fun clearHistory()
    fun showHistory()
    fun hideHistory()
    fun showEmptyResult()
    fun showTracks(trackList:List<Track>)
    fun showTracksError()
    fun StartShowTracks()
    fun clearSearchText()
    fun hideKeyboard()
    fun hideTracks()
    fun goBack()
    fun hideYourText()
    fun showYourText()
    fun historyListRemove(track: Track)
    fun historyListRemoveAt()
    fun historyListAdd()
}