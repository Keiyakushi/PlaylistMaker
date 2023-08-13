package com.practicum.playlistmaker.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.activity.PlaylistAdapter
import com.practicum.playlistmaker.media.data.ResultStates
import com.practicum.playlistmaker.media.view_model.PlaylistsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var playlistAdapter: PlaylistAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initObservers()
    }

    private fun initAdapter() {
        playlistAdapter = PlaylistAdapter {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = playlistAdapter
    }

    private fun initObservers() {
        binding.btAddPlaylists.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistsFragment)
        }
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.showContent.collect {
                when (it) {
                    is ResultStates.Empty -> {
                        binding.emptyMedia.visibility = View.VISIBLE
                        binding.textEmptyPlaylists.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                    is ResultStates.HasPlaylists -> {
                        binding.emptyMedia.visibility = View.GONE
                        binding.textEmptyPlaylists.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        playlistAdapter.apply {
                            playlist.clear()
                            playlist.addAll(it.result)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}