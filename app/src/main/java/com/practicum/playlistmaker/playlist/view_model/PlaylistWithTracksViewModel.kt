package com.practicum.playlistmaker.playlist.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.playlist.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.settings.domain.IRouterInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlaylistWithTracksViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val router: IRouterInteractor,
) : ViewModel() {

    private val _getPlaylist = MutableLiveData<ArrayList<Track>>()
    val getPlaylist = _getPlaylist
    private val _hasTracks = MutableLiveData<Boolean>()
    val hasTracks = _hasTracks
    private val _getPlaylistData = MutableLiveData<Playlist>()
    val getPlaylistData = _getPlaylistData
    private var playlists: Playlist? = null
    fun fillData(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id)
                .collect { playlist ->
                    processResult(playlist.trackList)
                    playlists = playlist
                    _getPlaylistData.value = playlist
                }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlist, track)
        }
    }

    fun getName(): String {
        return playlists?.playlistName ?: ""
    }

    fun getDescription(): String {
        return playlists?.playlistDescription ?: ""
    }

    fun getImage(): String {
        return playlists?.imageUrl ?: ""
    }

    fun getCounts(): String {
        if (playlists?.countTracks == null) {
            return "0 треков"
        } else {
            return playlists?.countTracks.toString() + " " + playlists?.let {
                fixNumerical(
                    it.countTracks,
                    arrayOf("трек", "трека", "треков"))
            }
        }
    }

    fun getDuration(): String {
        var sum: Int = 0
        playlists?.trackList?.forEach {
            sum += it.trackTimeMillis?.toInt() ?: 0
        }
        var duration = SimpleDateFormat("m",
            Locale.getDefault()).format(sum.toLong()).toString()
        return duration + " " + fixNumerical(duration.toInt(), arrayOf("минута", "минуты", "минут"))
    }

    fun share() {
        var counter = 0
        var message = playlists?.playlistName + "\n" + playlists?.playlistDescription + "\n" +
                playlists?.countTracks.toString() + " " + playlists?.let {
            fixNumerical(
                it.countTracks,
                arrayOf("трек", "трека", "треков"))
        }
        playlists?.trackList?.forEach { track ->
            counter += 1
            var time = SimpleDateFormat("mm:ss",
                Locale.getDefault()).format(track.trackTimeMillis?.toLong())
            message += "\n$counter." + track.artistName + "-" + track.trackName + "($time)"
        }
        router.shareApp(message)
    }

    private fun fixNumerical(num: Int, text: Array<String>): String {
        return if (num % 100 in 5..20) text[2] else
            if (num % 10 == 1) text[0] else
                if (num % 10 in 2..4) text[1]
                else text[2]
    }

    private fun processResult(trackList: List<Track>) {
        if (trackList.isEmpty()) {
            _hasTracks.value = false
        } else {
            _hasTracks.value = true
            _getPlaylist.value = trackList as ArrayList<Track>
        }
    }
}