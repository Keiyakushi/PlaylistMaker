package com.practicum.playlistmaker.playlist.framents

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistWithTracksBinding
import com.practicum.playlistmaker.player.activity.MediaPlayerFragment
import com.practicum.playlistmaker.playlist.activity.PlaylistWithTracksAdapter
import com.practicum.playlistmaker.playlist.data.Playlist
import com.practicum.playlistmaker.playlist.view_model.PlaylistWithTracksViewModel
import com.practicum.playlistmaker.search.activity.TrackAdapter
import com.practicum.playlistmaker.search.data.Track
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class PlaylistWithTracksFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistWithTracksBinding
    private lateinit var playlist: Playlist
    private val viewModel by viewModel<PlaylistWithTracksViewModel>()
    private lateinit var trackAdapter: PlaylistWithTracksAdapter
    private var hasTrack : Boolean = false
    private lateinit var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>
    override fun onResume() {
        super.onResume()
        viewModel.fillData(playlist.id)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPlaylistWithTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.shareBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        fillData()
        initAdapter()
        initObservers()
        initListeners()
    }

    private fun initListeners(){
        binding.backIconMedia.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.share.setOnClickListener {
            share()
        }
        binding.more.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.shareSecond.setOnClickListener {
            share()
        }
        binding.deletePlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDialogDeletePlaylist(playlist)
        }
        binding.changeInfo.setOnClickListener {
            findNavController().navigate(R.id.action_playlistWithTracks_to_createPlaylistsFragment,
                CreatePlaylistsFragment.createArgs(playlist))
        }
    }

    private fun initAdapter(){
        trackAdapter = PlaylistWithTracksAdapter({ track -> navigate(track) }
        ) { showDialog(playlist,it)}
        binding.recyclerView.adapter = trackAdapter
    }
    private fun initObservers(){
        viewModel.hasTracks.observe(viewLifecycleOwner){
            if (it){
                binding.emptyPlaylist.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                hasTrack = true
            } else{
                binding.emptyPlaylist.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                hasTrack = false
            }
        }
        viewModel.getPlaylist.observe(viewLifecycleOwner){
            trackAdapter.trackAdapterList.clear()
            trackAdapter.trackAdapterList.addAll(it)
            trackAdapter.notifyDataSetChanged()
            refreshInfo()
        }
        viewModel.getPlaylistData.observe(viewLifecycleOwner){
            playlist = it
            refreshInfo()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fillData(){
        playlist = requireArguments().getString(ARGS_TRACK)?.let {
            Json.decodeFromString<Playlist>(it)
        }!!
        viewModel.fillData(playlist.id)
        binding.playlistName.text = playlist.playlistName
        binding.description.text = playlist.playlistDescription
        Glide.with(this)
            .load(playlist.imageUrl)
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .into(binding.setImage)
        Glide.with(this)
            .load(playlist.imageUrl)
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_album_cover)))
            .into(binding.artwork)
        binding.countTracksBottomSheet.text = viewModel.getCounts()
        binding.duration.text = viewModel.getDuration()
        binding.countTracks.text = viewModel.getCounts()
        binding.trackName.text = playlist.playlistName
    }
    private fun navigate(it : Track){
        findNavController().navigate(R.id.action_playlistWithTracks_to_mediaPlayerFragment,MediaPlayerFragment.createArgs(it))
    }
    private fun showDialog(playlist: Playlist,track: Track) {
        MaterialAlertDialogBuilder(requireContext(),R.style.DialogWithBlueButtons)
            .setMessage("Хотите удалить трек?")
            .setNeutralButton("НЕТ") { _, _ -> }
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteTrackFromPlaylist(playlist, track)
                refreshInfo()
            }
            .show()
    }
    private fun showDialogDeletePlaylist(playlist: Playlist){
        MaterialAlertDialogBuilder(requireContext(),R.style.DialogWithBlueButtons)
            .setMessage("Хотите удалить плейлист «${playlist.playlistName}»?")
            .setNeutralButton("НЕТ") { _, _ -> }
            .setPositiveButton("ДА") { _, _ ->
                findNavController().navigateUp()
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deletePlaylist(playlist)
                }
            }
            .show()
    }
    private fun refreshInfo(){
        binding.countTracks.text = viewModel.getCounts()
        binding.duration.text = viewModel.getDuration()
        binding.countTracksBottomSheet.text = viewModel.getCounts()
        binding.playlistName.text = viewModel.getName()
        binding.description.text = viewModel.getDescription()
        Glide.with(this)
            .load(viewModel.getImage())
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .into(binding.setImage)
    }
    private fun share(){
        if (!hasTrack){
            Toast.makeText(requireContext(),getString(R.string.cant_share),Toast.LENGTH_SHORT).show()
        } else {
            viewModel.share()
        }
    }
    companion object {
        private const val ARGS_TRACK = "args_track"
        fun createArgs(playlist: Playlist): Bundle =
            bundleOf(ARGS_TRACK to Json.encodeToString(playlist))
    }
}