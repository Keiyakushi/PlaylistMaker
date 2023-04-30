package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.Track

interface SearchScreenView {
    fun clearHistory()
    fun showHistory()
    fun hideHistory()
    fun showEmptyResult()
    fun showTracks(trackList:ArrayList<Track>)
    fun showTracksError()
    fun StartShowTracks()
    fun clearSearchText()
    fun hideKeyboard()
    fun hideTracks()
    fun goBack()
    fun hideYourText()
    fun showYourText()
    fun historyListRemove(track:Track)
    fun historyListRemoveAt()
    fun historyListAdd()
}