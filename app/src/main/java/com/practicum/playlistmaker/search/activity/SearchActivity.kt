package com.practicum.playlistmaker.search.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.data.HandlerR
import com.practicum.playlistmaker.router.Router
import com.practicum.playlistmaker.search.data.SearchState
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.view_model.SearchScreenView
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchActivity : AppCompatActivity(), SearchScreenView {
    companion object {
        const val MEDIA_KEY = "MEDIA_KEY"
        const val SEARCH_DEBOUNCE_DELAY_MS = 2000L
        const val CLICK_DEBOUNCE_DELAY_MS = 1000L
    }

    private val historyList = ArrayList<Track>()
    private val trackList = ArrayList<Track>()
    var isClickAllowed = true
    var savedText: String = ""
    private val viewModel: SearchViewModel by viewModel {
        parametersOf(historyList)
    }
    private lateinit var trackAdapter: TrackAdapter
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }

    override fun onStop() {
        super.onStop()
        viewModel.addAllToSaveHistory(historyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.addAllToSaveHistory(historyList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        trackAdapter = initTrackAdapter(binding.recyclerView)
        initTrackHistoryAdapter()

        viewModel.ClearHistoryListLiveData.observe(this) { historyList ->
            if (historyList.isEmpty()) {
                hideHistory()
            } else {
                showHistory()
            }
        }
        viewModel.StartShowTracks.observe(this) {
            when (it) {
                SearchState.PrepareShowTracks -> StartShowTracks()
                SearchState.SearchTextClear -> {
                    hideKeyboard()
                    hideTracks()
                    clearSearchText()
                }
                SearchState.ShowEmptyResult -> showEmptyResult()
                SearchState.ShowTracksError -> showTracksError()
            }
        }
        viewModel.TracksListLiveData.observe(this) {
            showTracks(it)
        }
        viewModel.VisbilityHistory.observe(this) {
            if (it) {
                showHistory()
            } else {
                hideHistory()
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusSearchChanged(hasFocus, binding.searchEditText.text.toString())
        }
        binding.btHistoryClear.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.backIconSearch.setOnClickListener {
            goBack()
        }
        binding.clearText.setOnClickListener {
            viewModel.SearchTextClearClicked()
        }
        binding.btUpdate.setOnClickListener {
            viewModel.loadTracks(savedText)
        }

        val searchRunnable = Runnable { viewModel.loadTracks(savedText) }

        fun searchDebounce() {
            getKoin().get<HandlerR>().get().removeCallbacks(searchRunnable)
            getKoin().get<HandlerR>().get().postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MS)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearText.visibility =
                    if (p0.isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                savedText = p0.toString()
                searchDebounce()
                viewModel.hasTextOnWatcher(savedText)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        historyList.addAll(viewModel.addAllToHistory())
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initTrackHistoryAdapter() {
        val trackHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                binding.historySearchList.adapter?.notifyDataSetChanged()
                addTrackToHistory(it)
                getKoin().get<Router>().addToMedia(it, this)
            }
        }
        trackHistoryAdapter.trackAdapterList = historyList
        binding.historySearchList.adapter = trackHistoryAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initTrackAdapter(recyclerView: RecyclerView): TrackAdapter {
        val trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                binding.historySearchList.adapter?.notifyDataSetChanged()
                addTrackToHistory(it)
                getKoin().get<Router>().addToMedia(it, this)
            }
        }
        trackAdapter.trackAdapterList = trackList
        recyclerView.adapter = trackAdapter
        return trackAdapter
    }

    private fun addTrackToHistory(track: Track) = viewModel.addTrackToHistory(track)

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            getKoin().get<HandlerR>().get()
                .postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MS)
        }
        return current
    }

    override fun clearHistory() {
        if (historyList.isEmpty())
            binding.historyLayout.visibility = View.GONE
    }

    override fun showHistory() {
        binding.historyLayout.visibility = View.VISIBLE
    }

    override fun hideHistory() {
        binding.historyLayout.visibility = View.GONE
    }

    override fun showEmptyResult() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.VISIBLE
        binding.iwNoConnectionLayout.visibility = View.GONE
    }

    override fun showTracks(trackList: List<Track>) {
        binding.progressBar.visibility = View.GONE
        trackAdapter.trackAdapterList.clear()
        trackAdapter.trackAdapterList.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
        binding.recyclerView.visibility = View.VISIBLE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
    }

    override fun showTracksError() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.VISIBLE
    }

    override fun StartShowTracks() {
        binding.recyclerView.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun clearSearchText() {
        binding.searchEditText.setText("")
    }

    override fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.clearText.windowToken, 0)
    }

    override fun hideTracks() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
    }

    override fun goBack() {
        finish()
    }

    override fun hideYourText() {
        binding.btHistoryClear.visibility = View.GONE
        binding.yourSearchText.visibility = View.GONE
    }

    override fun showYourText() {
        binding.btHistoryClear.visibility = View.VISIBLE
        binding.yourSearchText.visibility = View.VISIBLE
    }

    override fun historyListRemove(track: Track) {
        binding.historySearchList.adapter?.notifyItemRemoved(historyList.indexOf(track))
        binding.historySearchList.adapter?.notifyItemRangeChanged(historyList.indexOf(track),
            historyList.size)
    }

    override fun historyListRemoveAt() {
        binding.historySearchList.adapter?.notifyItemRemoved(9)
        binding.historySearchList.adapter?.notifyItemRangeChanged(9, historyList.size)
    }

    override fun historyListAdd() {
        binding.historySearchList.adapter?.notifyItemInserted(0)
        binding.historySearchList.adapter?.notifyItemRangeChanged(0, historyList.size)
    }
}