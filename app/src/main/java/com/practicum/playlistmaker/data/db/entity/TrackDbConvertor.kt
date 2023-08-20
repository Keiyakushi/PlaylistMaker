package com.practicum.playlistmaker.data.db.entity

import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.search.data.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId,
            previewUrl = track.previewUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            track.artworkUrl60,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            date = Date().toString()
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            previewUrl = track.previewUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            track.artworkUrl60,
            trackId = track.id,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imageUrl = playlist.imageUrl,
            trackList = Json.decodeFromString(playlist.trackIdList),
            countTracks = playlist.countTracks)
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imageUrl = playlist.imageUrl,
            trackIdList = Json.encodeToString(playlist.trackList),
            countTracks = playlist.countTracks,
            saveDate = Date().toString()
        )
    }
    fun mapTrackToPlaylist(track: Track) : TrackToPlaylistsEntity{
        return TrackToPlaylistsEntity(
            id = track.trackId,
            previewUrl = track.previewUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            track.artworkUrl60,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
            date = Date().toString()
        )
    }
    fun mapTrackToPlaylist(track: TrackToPlaylistsEntity) : Track{
        return Track(
            previewUrl = track.previewUrl,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            track.artworkUrl60,
            trackId = track.id,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,
        )
    }
}