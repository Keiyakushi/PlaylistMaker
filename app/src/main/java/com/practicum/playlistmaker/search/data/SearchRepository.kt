package com.practicum.playlistmaker.search.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.domain.ISearchRepository
import com.practicum.playlistmaker.search.domain.NetworkError
import com.practicum.playlistmaker.search.domain.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


class SearchRepository(val api: Network, val searchHistory: SearchHistory, val context: Context) :
    ISearchRepository {

    override suspend fun doRequest(query: String): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }

        } else {
            val response = withContext(Dispatchers.IO) {
                try {
                    api.api.search(query)
                } catch (e: Throwable) {
                    null
                }
            }
            return if (response?.body() != null) {
                when (response.code()) {
                    in 400..499 -> Response().apply { resultCode = 400 }

                    in 500..599 -> {
                        Response().apply { resultCode = 500 }
                    }

                    else -> {
                        response.body()?.apply { resultCode = 200 }!!
                    }
                }
            } else {
                Response().apply {
                    resultCode = 400
                }
            }
        }
    }

    override fun loadTracks(query: String): Flow<Resource> = flow {

        val response = doRequest(query)

        when (response.resultCode) {

            -1, 500 -> emit(Resource.Error(NetworkError.CONNECTION_ERROR))
            400 -> emit(Resource.Error(NetworkError.SEARCH_ERROR))

            else -> {
                val resultList = (response as TrackResponse).results
                if (resultList.isEmpty()) {
                    emit(Resource.Error(NetworkError.SEARCH_ERROR))
                } else {
                    val trackList = resultList
                    emit(Resource.Success(trackList as ArrayList<Track>))
                }
            }
        }
    }

    override fun readHistory(): Array<Track> {
        return searchHistory.readHistory()
    }

    override fun saveHistory(historyList: ArrayList<Track>) {
        searchHistory.saveHistory(historyList)
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> return true
            }
        }
        return false
    }
}