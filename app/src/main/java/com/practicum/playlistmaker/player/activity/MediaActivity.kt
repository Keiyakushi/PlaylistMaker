package com.practicum.playlistmaker.player.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.view_model.PlayerView
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.player.view_model.PlayerViewModelFactory
import com.practicum.playlistmaker.router.Router
import java.text.SimpleDateFormat
import java.util.*

class MediaActivity : AppCompatActivity(), PlayerView {
    companion object {
        const val MEDIA_KEY = "MEDIA_KEY"
    }

    private val binding by lazy { ActivityMediaBinding.inflate(layoutInflater) }
    lateinit var playerRepository: PlayerRepository
    lateinit var interactor: MediaPlayerInteractor
    val router = Router(this)
    private lateinit var viewModel: PlayerViewModel
    val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        playerRepository = PlayerRepository(router.getTrack().previewUrl)
        interactor = MediaPlayerInteractor(playerRepository)
        viewModel = ViewModelProvider(this, PlayerViewModelFactory(
            interactor, mainThreadHandler)
        )[PlayerViewModel::class.java]
        viewModel.state.observe(this) { state ->
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
        viewModel.SetTime.observe(this) {
            setCurrentTime(it)
        }

        binding.btPlay.setOnClickListener {
            viewModel.onBtPlayClicked()
        }

        binding.backIconMedia.setOnClickListener {
            finish()
        }
    }

    override fun btPlayAllowed() {
        binding.btPlay.isEnabled = true
    }

    private fun btPlayBan() {
        binding.btPlay.isEnabled = false
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
        var track = router.getTrack()
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_album_cover)))
            .into(binding.coverMedia)
        binding.trackNameMedia.text = track.trackName
        binding.artistNameMedia.text = track.artistName
        binding.timeDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        binding.albumName.text = track.collectionName
        binding.yearScore.text = track.releaseDate.substring(0, 4)
        binding.genreName.text = track.primaryGenreName
        binding.countryName.text = track.country
        binding.btPlay.isEnabled = false
    }

    override fun goBack() {
        finish()
    }

    override fun btPauseSetImage() {
        binding.btPlay.setImageResource(R.drawable.ic_bt_pause)
    }
}
