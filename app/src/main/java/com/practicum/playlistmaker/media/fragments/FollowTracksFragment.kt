package com.practicum.playlistmaker.media.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentFollowTracksBinding
import com.practicum.playlistmaker.media.view_model.FollowTracksViewModel
import com.practicum.playlistmaker.router.Router
import com.practicum.playlistmaker.search.activity.TrackAdapter
import com.practicum.playlistmaker.search.data.Track
import com.practicum.playlistmaker.ui.root.search.SearchFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowTracksFragment : Fragment() {
    private val viewModel by viewModel<FollowTracksViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private var trackList = ArrayList<Track>()
    private lateinit var binding: FragmentFollowTracksBinding
    private var isClickAllowed = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFollowTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAdapter = initTrackAdapter(binding.recyclerView)
        viewModel.fillData()
        viewModel.setFollow.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.getTrack.observe(viewLifecycleOwner) {
            showFavTracks(it)
        }
    }

    private fun initTrackAdapter(recyclerView: RecyclerView): TrackAdapter {
        trackAdapter = TrackAdapter {
            if (clickDebounce()) {
                getKoin().get<Router>().addToMedia(it, requireActivity() as AppCompatActivity)
            }
        }
        trackAdapter.trackAdapterList = trackList
        recyclerView.adapter = trackAdapter
        return trackAdapter
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(SearchFragment.CLICK_DEBOUNCE_DELAY_MS)
                isClickAllowed = true
            }
        }
        return current
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showFavTracks(track: ArrayList<Track>) {
        trackList.clear()
        trackList.addAll(track)
        trackAdapter.notifyDataSetChanged()
    }

    fun render(it: Boolean) {
        if (it) {
            binding.emptyMedia.visibility = View.VISIBLE
            binding.emptyMediaText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyMedia.visibility = View.GONE
            binding.emptyMediaText.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance() = FollowTracksFragment()
    }
}