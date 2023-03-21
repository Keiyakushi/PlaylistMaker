package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
enum class SearchStatus { SUCCESS, CONNECTION_ERROR, EMPTY_SEARCH,START }
class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val MEDIA_KEY = "MEDIA_KEY"
    }
    private val historyList = ArrayList<Track>()
    private val trackList = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)
    private lateinit var inputText: EditText
    private lateinit var searchHistory: SearchHistory
    private lateinit var historySearchList: RecyclerView
    var savedText: String = ""


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(SEARCH_TEXT, "")
        inputText = findViewById(R.id.search_edit_text)
        inputText.setText(savedText)
    }

    override fun onStop() {
        super.onStop()
        searchHistory.saveHistory(historyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchHistory.saveHistory(historyList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val clearButton = findViewById<ImageView>(R.id.clear_text)
        inputText = findViewById(R.id.search_edit_text)
        val image = findViewById<ImageView>(R.id.back_icon_search)
        val reloadButton = findViewById<Button>(R.id.bt_update)
        val yourSearchText = findViewById<TextView>(R.id.your_search_text)
        val clearHistoryButton = findViewById<Button>(R.id.bt_history_clear)
        historySearchList = findViewById(R.id.history_search_list)
        val historyLayout = findViewById<LinearLayout>(R.id.history_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val trackAdapter = TrackAdapter{
            addTrackToHistory(it)
            addToMedia(it)
        }
        trackAdapter.trackAdapterList = trackList
        recyclerView.adapter = trackAdapter
        searchHistory = SearchHistory(getSharedPreferences(PREFERENCES, MODE_PRIVATE))
        val trackHistoryAdapter = TrackAdapter {
            addTrackToHistory(it)
            addToMedia(it)
        }
        trackHistoryAdapter.trackAdapterList = historyList
        historySearchList.adapter = trackHistoryAdapter


        inputText.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus && inputText.text.isEmpty() && historyList.isNotEmpty()){
                historyLayout.visibility = View.VISIBLE
            } else historyLayout.visibility = View.GONE
        }
        clearHistoryButton.setOnClickListener{
            historyList.clear()
            historySearchList.adapter?.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }



        image.setOnClickListener {
            finish()
        }
        clearButton.setOnClickListener {
            inputText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            search(SearchStatus.START)
        }
        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        fun responseTrack(searchText: String) {
            if (searchText.isNotEmpty()) {
                iTunesService.search(searchText).enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                search(SearchStatus.SUCCESS)
                            }
                            if (trackList.isEmpty()) {
                                search(SearchStatus.EMPTY_SEARCH)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        search(SearchStatus.CONNECTION_ERROR)
                        reloadButton.setOnClickListener {
                            responseTrack(savedText)
                        }
                    }

                })
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
                savedText = p0.toString()
                inputText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        responseTrack(savedText)
                        true
                    }
                    false
                }
                historyLayout.visibility =
                    if (inputText.text.isEmpty()) View.VISIBLE
                    else View.GONE
                    if (historyList.isEmpty()) {
                        clearHistoryButton.visibility = View.GONE
                        yourSearchText.visibility = View.GONE
                    } else {
                        clearHistoryButton.visibility = View.VISIBLE
                        yourSearchText.visibility = View.VISIBLE
                    }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        historyList.addAll(searchHistory.readHistory())
        inputText.addTextChangedListener(textWatcher)
    }
    private fun addToMedia(track:Track){
        val intent = Intent(this,MediaActivity::class.java).apply{
            putExtra(MEDIA_KEY, Gson().toJson(track))
        }
        startActivity(intent)
    }
    private fun addTrackToHistory(track: Track) = when {

        historyList.contains(track) -> {

            val position = historyList.indexOf(track)

            historyList.remove(track)
            historySearchList.adapter?.notifyItemRemoved(position)
            historySearchList.adapter?.notifyItemRangeChanged(position, historyList.size)

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }

        historyList.size < 10 -> {

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }

        else -> {

            historyList.removeAt(9)
            historySearchList.adapter?.notifyItemRemoved(9)
            historySearchList.adapter?.notifyItemRangeChanged(9, historyList.size)

            historyList.add(0, track)
            historySearchList.adapter?.notifyItemInserted(0)
            historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
        }
    }

    private fun search(search: SearchStatus) {
        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        val noResult = findViewById<FrameLayout>(R.id.iw_no_result_layout)
        val noConnection = findViewById<FrameLayout>(R.id.iw_no_connection_layout)
        when (search) {
            SearchStatus.SUCCESS -> {
                recycler.visibility = View.VISIBLE
                noResult.visibility = View.GONE
                noConnection.visibility = View.GONE
            }
            SearchStatus.CONNECTION_ERROR -> {
                recycler.visibility = View.GONE
                noResult.visibility = View.GONE
                noConnection.visibility = View.VISIBLE
            }
            SearchStatus.EMPTY_SEARCH -> {
                recycler.visibility = View.GONE
                noResult.visibility = View.VISIBLE
                noConnection.visibility = View.GONE
            }
            SearchStatus.START ->{
                recycler.visibility = View.GONE
                noResult.visibility = View.GONE
                noConnection.visibility = View.GONE
            }
        }
    }
}