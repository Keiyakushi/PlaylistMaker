package com.practicum.playlistmaker.main.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.model.MainStates

class MainViewModel : ViewModel(){
    private val _contentStateLiveData = SingleLiveEvent<MainStates>()
    val contentStateLiveData : LiveData<MainStates> = _contentStateLiveData

    fun onSearchButtonClicked() {
        _contentStateLiveData.postValue(MainStates.Search)
    }

    fun onLibraryButtonClicked() {
        _contentStateLiveData.postValue(MainStates.Library)
    }

    fun onSettingsButtonClicked() {
        _contentStateLiveData.postValue(MainStates.Settings)
    }
}