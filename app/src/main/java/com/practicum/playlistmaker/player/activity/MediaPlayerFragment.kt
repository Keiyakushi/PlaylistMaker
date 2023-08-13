package com.practicum.playlistmaker.player.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.media.data.ResultStates
import com.practicum.playlistmaker.media.data.ResultStatesBottomSheet
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.view_model.PlayerView
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class MediaPlayerFragment : Fragment(), PlayerView {
    companion object {
        const val MEDIA_KEY = "MEDIA_KEY"
        const val ARGS_TRACK = "args_track"
        fun createArgs(track: Track): Bundle =
            bundleOf(ARGS_TRACK to Json.encodeToString(track))
    }

    private lateinit var binding: ActivityMediaBinding
    private lateinit var track: Track
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter
    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = ActivityMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initObservers()
        initListeners()
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })
        binding.btAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun initAdapter() {
        playlistAdapter = PlaylistAdapter {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.addTrackToPlaylist(it, track)
            }
        }
        binding.recyclerView.adapter = playlistAdapter
    }

    private fun initObservers() {
        track = requireArguments().getString(ARGS_TRACK)?.let {
            Json.decodeFromString<Track>(it)
        } ?: Track.emptyTrack
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.showContent.collect {
                when (it) {
                    is ResultStates.Empty -> {
                        binding.recyclerView.visibility = View.GONE
                    }
                    is ResultStates.HasPlaylists -> {
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastContent.collect {
                when (it) {
                    is ResultStatesBottomSheet.AlreadyHas -> {
                        Toast.makeText(requireContext(),
                            "Трек уже добавлен в плейлист ${it.result.playlistName}",
                            Toast.LENGTH_SHORT).show()
                    }
                    is ResultStatesBottomSheet.NotHas -> {
                        Toast.makeText(requireContext(),
                            "Добавлено в плейлист ${it.result.playlistName} ",
                            Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerStatus.OnComplete -> {
                    btPlaySetImage()
                    setTimeZero()
                }
                PlayerStatus.OnPrepare -> btPlayAllowed()
                PlayerStatus.SetPauseImage -> btPauseSetImage()
                PlayerStatus.SetPlayImage -> {
                    btPlaySetImage()
                    btPlayAllowed()
                }
                PlayerStatus.SetTimeZero -> {
                    setTimeZero()
                }
            }
        }
        getData()
        viewModel.isFavorite(track.trackId)
        viewModel.preparePlayer(track.previewUrl)
        viewModel.setFollow.observe(viewLifecycleOwner) {
            if (it) {
                binding.btFollow.setImageResource(R.drawable.ic_bt_follow)
            } else {
                binding.btFollow.setImageResource(R.drawable.ic_button_follow_red)
            }
        }
        viewModel.isFavorite.observe(viewLifecycleOwner) {
            if (!it) {
                binding.btFollow.setImageResource(R.drawable.ic_bt_follow)
            } else {
                binding.btFollow.setImageResource(R.drawable.ic_button_follow_red)
            }
        }

        viewModel.setTime.observe(viewLifecycleOwner) {
            setCurrentTime(it)
        }
    }

    private fun initListeners() {
        binding.btPlay.setOnClickListener {
            viewModel.onBtPlayClicked(track.previewUrl)
        }

        binding.btFollow.setOnClickListener {
            viewModel.onBtFavoriteClicked(track)
        }

        binding.backIconMedia.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btAddPlaylists.setOnClickListener {
            findNavController().navigate(R.id.action_mediaPlayerFragment_to_createPlaylistsFragment)
        }
    }


    override fun btPlayAllowed() {
        binding.btPlay.isEnabled = true
    }

    override fun btPlaySetImage() {
        binding.btPlay.setImageResource(R.drawable.ic_bt_play)
    }

    override fun setTimeZero() {
        binding.timing.text = "00:00"
    }

    fun setCurrentTime(time: Int) {
        binding.timing.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(time)
    }

    override fun getData() {
        track = track
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_album_cover)))
            .into(binding.coverMedia)
        binding.trackNameMedia.text = track.trackName
        binding.artistNameMedia.text = track.artistName
        binding.timeDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?.toLong())
        binding.albumName.text = track.collectionName
        binding.yearScore.text = track.releaseDate.substring(0, 4)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country
        binding.btPlay.isEnabled = true

    }

    override fun goBack() {
        findNavController().navigateUp()
    }

    override fun btPauseSetImage() {
        binding.btPlay.setImageResource(R.drawable.ic_bt_pause)
    }
}
