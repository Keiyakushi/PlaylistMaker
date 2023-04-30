package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.player.MediaActivity
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.SearchRepository
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.presentation.SearchPresenter
import com.practicum.playlistmaker.search.presentation.SearchScreenView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
enum class SearchStatus { SUCCESS, CONNECTION_ERROR, EMPTY_SEARCH,START }
class SearchActivity : AppCompatActivity(),SearchScreenView {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val MEDIA_KEY = "MEDIA_KEY"
        const val SEARCH_DEBOUNCE_DELAY_MS = 2000L
        const val CLICK_DEBOUNCE_DELAY_MS = 1000L
    }
    private val historyList = ArrayList<Track>()
    private val trackList = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    var isClickAllowed = true
    val handler = Handler(Looper.getMainLooper())
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val repository = SearchRepository(retrofit.create(iTunesApi::class.java))
    private lateinit var progressBar: ProgressBar
    private lateinit var inputText: EditText
    private lateinit var searchHistory: SearchHistory
    private lateinit var historySearchList: RecyclerView
    private lateinit var historyLayout : LinearLayout
    private lateinit var clearButton : ImageView
    private lateinit var yourSearchText : TextView
    private lateinit var clearHistoryButton : Button
    private lateinit var recyclerView : RecyclerView
    var savedText: String = ""
    private lateinit var trackAdapter : TrackAdapter
    private lateinit var recycler : RecyclerView
    private lateinit var noResult : FrameLayout
    private lateinit var noConnection : FrameLayout
    private lateinit var presenter : SearchPresenter
    private lateinit var imageback : ImageView
    private lateinit var reloadButton : Button
    private lateinit var interactor: SearchInteractor
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
        init()
        interactor = SearchInteractor(repository)
        trackAdapter = initTrackAdapter(recyclerView)
        initTrackHistoryAdapter()

        presenter = SearchPresenter(this,historyList,interactor)

        inputText.setOnFocusChangeListener { _, hasFocus ->
            presenter.onFocusSearchChanged(hasFocus,inputText.text.toString())
        }
        clearHistoryButton.setOnClickListener{
            presenter.onHistoryClearClicked()
        }
        imageback.setOnClickListener {
            presenter.goBack()
        }
        clearButton.setOnClickListener {
            presenter.searchTextClearClicked()
        }
        reloadButton.setOnClickListener(){
            presenter.loadTracks(savedText)
        }
        val searchRunnable = Runnable {presenter.loadTracks(savedText)}
        fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility =
                    if (p0.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                savedText = p0.toString()
                searchDebounce()
                presenter.hasTextOnWatcher(inputText.text.toString())
                presenter.textWatcherNavigation(historyList)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        historyList.addAll(searchHistory.readHistory())
        inputText.addTextChangedListener(textWatcher)
    }

    private fun init() {
        clearButton = findViewById(R.id.clear_text)
        inputText = findViewById(R.id.search_edit_text)
        imageback = findViewById(R.id.back_icon_search)
        reloadButton = findViewById(R.id.bt_update)
        yourSearchText = findViewById(R.id.your_search_text)
        clearHistoryButton = findViewById(R.id.bt_history_clear)
        historySearchList = findViewById(R.id.history_search_list)
        historyLayout = findViewById(R.id.history_layout)
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progressBar)
        searchHistory = SearchHistory(getSharedPreferences(PREFERENCES, MODE_PRIVATE))
        recycler = findViewById(R.id.recycler_view)
        noResult = findViewById(R.id.iw_no_result_layout)
        noConnection = findViewById(R.id.iw_no_connection_layout)
    }

    private fun initTrackHistoryAdapter() {
        val trackHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                addTrackToHistory(it)
                addToMedia(it)
            }
        }
        trackHistoryAdapter.trackAdapterList = historyList
        historySearchList.adapter = trackHistoryAdapter
    }

    private fun initTrackAdapter(recyclerView: RecyclerView) : TrackAdapter{
        val trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                addTrackToHistory(it)
                addToMedia(it)
            }
        }
        trackAdapter.trackAdapterList = trackList
        recyclerView.adapter = trackAdapter
        return trackAdapter
    }

    private fun addToMedia(track:Track){
        val intent = Intent(this, MediaActivity::class.java).apply{
            putExtra(MEDIA_KEY, Gson().toJson(track))
        }
        startActivity(intent)
    }
    private fun addTrackToHistory(track: Track) = presenter.addTrackToHistory(track)

    fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MS)
        }
        return current
    }

    override fun clearHistory() {
        historyList.clear()
        historySearchList.adapter?.notifyDataSetChanged()
        if (historyList.isEmpty())
        historyLayout.visibility = View.GONE
    }

    override fun showHistory() {
        historyLayout.visibility = View.VISIBLE
    }

    override fun hideHistory() {
        historyLayout.visibility = View.GONE
    }

    override fun showEmptyResult() {
        progressBar.visibility = View.GONE
        recycler.visibility = View.GONE
        noResult.visibility = View.VISIBLE
        noConnection.visibility = View.GONE
    }

    override fun showTracks(trackList:ArrayList<Track>) {
        progressBar.visibility = View.GONE
        trackAdapter.trackAdapterList.clear()
        trackAdapter.trackAdapterList.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
        recycler.visibility = View.VISIBLE
        noResult.visibility = View.GONE
        noConnection.visibility = View.GONE
    }

    override fun showTracksError() {
        progressBar.visibility = View.GONE
        recycler.visibility = View.GONE
        noResult.visibility = View.GONE
        noConnection.visibility = View.VISIBLE
    }

    override fun StartShowTracks() {
        recycler.visibility = View.GONE
        noResult.visibility = View.GONE
        noConnection.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun clearSearchText() {
        inputText.setText("")
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
    }

    override fun hideTracks() {
        progressBar.visibility = View.GONE
        recycler.visibility = View.GONE
        noResult.visibility = View.GONE
        noConnection.visibility = View.GONE
    }

    override fun goBack() {
        finish()
    }

    override fun hideYourText() {
        clearHistoryButton.visibility = View.GONE
        yourSearchText.visibility = View.GONE
    }

    override fun showYourText() {
        clearHistoryButton.visibility = View.VISIBLE
        yourSearchText.visibility = View.VISIBLE
    }

    override fun historyListRemove(track: Track) {
        historySearchList.adapter?.notifyItemRemoved(historyList.indexOf(track))
        historySearchList.adapter?.notifyItemRangeChanged(historyList.indexOf(track), historyList.size)
    }

    override fun historyListRemoveAt() {
        historySearchList.adapter?.notifyItemRemoved(9)
        historySearchList.adapter?.notifyItemRangeChanged(9, historyList.size)
    }

    override fun historyListAdd() {
        historySearchList.adapter?.notifyItemInserted(0)
        historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
    }
}