package com.practicum.playlistmaker.data.db.entity

import com.practicum.playlistmaker.search.data.Track
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
            trackId = track.id,
            collectionName = track.collectionName,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            releaseDate = track.releaseDate,)
    }
}