package com.practicum.playlistmaker.data.db.entity

import com.practicum.playlistmaker.search.data.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(track.trackId,track.previewUrl,track.trackName,track.artistName,
            track.trackTimeMillis,track.artworkUrl100,track.collectionName,track.country,
            track.primaryGenreName,track.releaseDate)
    }

    fun map(track: TrackEntity): Track {
        return Track(track.previewUrl,track.trackName,track.artistName,
            track.trackTimeMillis,track.artworkUrl100,track.id,track.collectionName,track.country,
            track.primaryGenreName,track.releaseDate)
    }
}