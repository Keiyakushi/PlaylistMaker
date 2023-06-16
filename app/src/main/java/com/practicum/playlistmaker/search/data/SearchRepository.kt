package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.ISearchRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(val api: Network, val searchHistory: SearchHistory) : ISearchRepository {

    override fun loadTracks(
        query: String,
        onSuccess: (ArrayList<Track>) -> Unit,
        onError: () -> Unit,
    ) {
        if (query.isNotEmpty()) {
            api.provideApi().search(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>,
                ) {
                    if (response.code() == 200) {
                        onSuccess.invoke(response.body()?.results!! as ArrayList<Track>)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    onError.invoke()
                }

            })
        }
    }

    override fun readHistory(): Array<Track> {
        return searchHistory.readHistory()
    }

    override fun saveHistory(historyList: ArrayList<Track>) {
        searchHistory.saveHistory(historyList)
    }
}