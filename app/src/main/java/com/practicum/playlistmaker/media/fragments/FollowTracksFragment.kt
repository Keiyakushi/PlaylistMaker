package com.practicum.playlistmaker.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentFollowTracksBinding
import com.practicum.playlistmaker.media.view_model.FollowTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowTracksFragment : Fragment() {
    private val viewModel by viewModel<FollowTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentFollowTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FollowTracksFragment()
    }
}