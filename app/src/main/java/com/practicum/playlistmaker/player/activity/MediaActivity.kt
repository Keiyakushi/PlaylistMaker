package com.practicum.playlistmaker.player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.player.data.PlayerStatus
import com.practicum.playlistmaker.player.view_model.PlayerView
import com.practicum.playlistmaker.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.router.Router
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class MediaActivity : AppCompatActivity(), PlayerView {
    companion object {
        const val MEDIA_KEY = "MEDIA_KEY"
    }

    private val binding by lazy { ActivityMediaBinding.inflate(layoutInflater) }


    private val viewModel: PlayerViewModel by viewModel()

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
            viewModel.onBtPlayClicked(getKoin().get<Router>().getTrack(this).previewUrl)
        }

        binding.backIconMedia.setOnClickListener {
            finish()
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
        var track = getKoin().get<Router>().getTrack(this)
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
        binding.btPlay.isEnabled = true
    }

    override fun goBack() {
        finish()
    }

    override fun btPauseSetImage() {
        binding.btPlay.setImageResource(R.drawable.ic_bt_pause)
    }
}
