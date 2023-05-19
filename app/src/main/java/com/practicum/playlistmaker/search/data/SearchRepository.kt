package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.ISearchRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRepository(private val api: iTunesApi) : ISearchRepository {

    override fun loadTracks(query:String, onSuccess : (ArrayList<Track>) -> Unit, onError : () -> Unit){
        if (query.isNotEmpty()) {
            api.search(query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
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

}