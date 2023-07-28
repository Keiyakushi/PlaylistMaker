package com.practicum.playlistmaker.ui.root.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.router.Router
import com.practicum.playlistmaker.search.activity.TrackAdapter
import com.practicum.playlistmaker.search.data.SearchState
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.search.view_model.SearchScreenView
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment(), SearchScreenView {
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
    private lateinit var textWatcher: TextWatcher
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private var timerJob: Job? = null
    override fun onStop() {
        super.onStop()
        viewModel.addAllToSaveHistory(historyList)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.addAllToSaveHistory(historyList)
    }

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAdapter = initTrackAdapter(binding.recyclerView)
        trackHistoryAdapter = initTrackHistoryAdapter(binding.historySearchList)

        viewModel.clearHistoryListLiveData.observe(viewLifecycleOwner) {
            if (viewModel.addAllToHistory().isEmpty()) {
                hideHistory()
            } else {
                showHistory()
            }
        }
        viewModel.startShowTracks.observe(viewLifecycleOwner) {
            when (it) {
                SearchState.PrepareShowTracks -> StartShowTracks()
                SearchState.SearchTextClear -> {
                    hideKeyboard()
                    hideTracks()
                    clearSearchText()
                    if (viewModel.addAllToHistory().isNotEmpty()) {
                        showHistory()
                    }
                }
                SearchState.ShowEmptyResult -> showEmptyResult()
                SearchState.ShowTracksError -> showTracksError()
            }
        }
        viewModel.tracksListLiveData.observe(viewLifecycleOwner) {
            if (binding.searchEditText.text.toString().isNotEmpty()) {
                showTracks(it)
            }
        }
        viewModel.visibilityHistory.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.addAllToHistory().isNotEmpty()) {
                    showHistory()
                }
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
        binding.clearText.setOnClickListener {
            viewModel.searchTextClearClicked()
        }
        binding.btUpdate.setOnClickListener {
            viewModel.loadTracks(savedText)
        }

        fun searchDebounce() {
            timerJob?.cancel()
            timerJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY_MS)
                viewModel.loadTracks(savedText)
            }
        }

        textWatcher = object : TextWatcher {
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
                if (p0.isNullOrEmpty()) {
                    if (historyList.isNotEmpty())
                        viewModel.hasTextOnWatcher(savedText)
                    return
                } else {
                    searchDebounce()
                }
                trackAdapter = initTrackAdapter(binding.recyclerView)
                viewModel.hasTextOnWatcher(savedText)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        historyList.addAll(viewModel.addAllToHistory())
        binding.searchEditText.addTextChangedListener(textWatcher)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initTrackHistoryAdapter(recyclerView: RecyclerView): TrackAdapter {
        trackHistoryAdapter = TrackAdapter {
            if (clickDebounce()) {
                binding.historySearchList.adapter?.notifyDataSetChanged()
                addTrackToHistory(it)
                getKoin().get<Router>().addToMedia(it, requireActivity() as AppCompatActivity)
            }
        }
        trackHistoryAdapter.trackAdapterList = historyList
        binding.historySearchList.adapter = trackHistoryAdapter
        return trackHistoryAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initTrackAdapter(recyclerView: RecyclerView): TrackAdapter {
        trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                binding.historySearchList.adapter?.notifyDataSetChanged()
                addTrackToHistory(it)
                getKoin().get<Router>().addToMedia(it, requireActivity() as AppCompatActivity)
            }
        }
        trackAdapter.trackAdapterList = trackList
        recyclerView.adapter = trackAdapter
        return trackAdapter
    }

    private fun addTrackToHistory(track: Track) = viewModel.addTrackToHistory(track)

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MS)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun clearHistory() {
        if (historyList.isEmpty())
            binding.historyLayout.visibility = View.GONE
    }

    override fun showHistory() {
        trackHistoryAdapter.trackAdapterList.clear()
        trackHistoryAdapter.trackAdapterList.addAll(viewModel.addAllToHistory())
        trackHistoryAdapter.notifyDataSetChanged()
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
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.clearText.windowToken, 0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun hideTracks() {
        trackAdapter.trackAdapterList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.iwNoResultLayout.visibility = View.GONE
        binding.iwNoConnectionLayout.visibility = View.GONE
    }

    override fun goBack() {
        requireActivity().finish()
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
