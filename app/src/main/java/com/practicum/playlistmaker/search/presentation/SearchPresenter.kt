package com.practicum.playlistmaker.search.presentation

import android.media.MediaPlayer
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.search.domain.ISearchInteractor


class SearchPresenter (
   private val view: SearchScreenView,
   private val historyList: ArrayList<Track>,
   private val interactor: ISearchInteractor
){
    fun onHistoryClearClicked(){
        view.clearHistory()
    }
    fun onFocusSearchChanged(hasFocus : Boolean, text: String){
        if(hasFocus && text.isEmpty() && historyList.isNotEmpty()){
            view.showHistory()
        } else view.hideHistory()
    }
    fun loadTracks(query: String){
        if (query.isEmpty()){
            return
        }
        view.StartShowTracks()
        interactor.loadTracks(
            query = query,
            onSuccess = { it ->
                if (it.isEmpty()){
                    view.showEmptyResult()
                } else {
                    view.showTracks(it)
                }
            },
            onError = {
                view.showTracksError()
            }
        )
    }
    fun goBack(){
        view.goBack()
    }
    fun searchTextClearClicked() {
        view.hideKeyboard()
        view.hideTracks()
        view.clearSearchText()
    }
    fun hasTextOnWatcher(query: String){
        if (query.isEmpty()){
            view.showHistory()
        } else view.hideHistory()
    }
    fun textWatcherNavigation(historyList: ArrayList<Track>){
        if (historyList.isEmpty()){
            view.hideYourText()
        } else view.showYourText()
    }
    fun addTrackToHistory(track: Track){
        when {
            historyList.contains(track) -> {
                historyList.remove(track)
                historyList.add(0, track)
                view.historyListRemove(track)
                view.historyListAdd()
            }

            historyList.size < 10 -> {
                historyList.add(0, track)
                view.historyListAdd()
            }

            else -> {
                historyList.removeAt(9)
                historyList.add(0, track)
                view.historyListRemoveAt()
                view.historyListAdd()
            }
        }
    }

}