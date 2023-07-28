package com.practicum.playlistmaker.media.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import kotlinx.coroutines.launch

class FollowTracksViewModel(val interactor: FavoriteInteractor) : ViewModel(){
    private val _setFollow = MutableLiveData<Boolean>()
    val setFollow = _setFollow
    private val _getTrack = MutableLiveData<ArrayList<Track>>()
    val getTrack = _getTrack
    init {
        fillData()
    }
    fun fillData() {
        viewModelScope.launch {
            interactor
                .getFavoriteTracks()
                .collect { trackList ->
                    processResult(trackList)
                }
        }
    }

    private fun processResult(trackList: List<Track>) {
        if (trackList.isEmpty()){
            _setFollow.postValue(true)
        }else{
            _setFollow.postValue(false)
            getTrack.postValue(trackList.reversed() as ArrayList<Track>)
        }
    }
}