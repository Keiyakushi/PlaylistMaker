package com.practicum.playlistmaker.player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.data.PlayerRepository
import com.practicum.playlistmaker.player.presenter.PlayerPresentor
import com.practicum.playlistmaker.player.presenter.PlayerView
import com.practicum.playlistmaker.player.presenter.Router
import java.text.SimpleDateFormat
import java.util.*

class MediaActivity : AppCompatActivity(),PlayerView {
    companion object{
        const val MEDIA_KEY = "MEDIA_KEY"
    }
    lateinit var bt_play: ImageButton
    lateinit var timing: TextView
    lateinit var presentor: PlayerPresentor
    lateinit var mediaPlayerInteractor: MediaPlayerInteractor
    lateinit var playerRepository: PlayerRepository
    lateinit var track_name : TextView
    lateinit var artist_name : TextView
    lateinit var duration : TextView
    lateinit var album_name : TextView
    lateinit var year_release : TextView
    lateinit var genre : TextView
    lateinit var country : TextView
    lateinit var layout_media : ConstraintLayout
    lateinit var image_album : ImageView
    lateinit var back_icon : ImageView
    lateinit var bt_add_playlist : ImageButton
    lateinit var bt_follow : ImageButton
    lateinit var interactor : MediaPlayerInteractor
    var trackDuration : Int = 30000
    var url : String = ""
    val router = Router(this)

    override fun onPause() {
        super.onPause()
        presentor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        presentor.destroyPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        uiInit()
        playerRepository = PlayerRepository(router.getUrl().previewUrl)
        interactor = MediaPlayerInteractor(playerRepository)
        presentor = PlayerPresentor(this,interactor)
        presentor.preparePlayer()

        bt_play.setOnClickListener {
            presentor.onBtPlayClicked()
        }

        back_icon.setOnClickListener{
            presentor.goBack()
        }

    }

    private fun uiInit() {
        layout_media = findViewById(R.id.layout_media)
        back_icon = findViewById(R.id.back_icon_media)
        image_album = findViewById(R.id.cover_media)
        track_name = findViewById(R.id.track_name_media)
        artist_name = findViewById(R.id.artist_name_media)
        bt_add_playlist = findViewById(R.id.bt_add_to_playlist)
        bt_play = findViewById(R.id.bt_play)
        bt_follow = findViewById(R.id.bt_follow)
        timing = findViewById(R.id.timing)
        duration = findViewById(R.id.time_duration)
        album_name = findViewById(R.id.album_name)
        year_release = findViewById(R.id.year_score)
        genre = findViewById(R.id.genre_name)
        country = findViewById(R.id.country_name)
    }


    override fun btPlayAllowed() {
        bt_play.isEnabled = true
    }

    override fun btPlaySetImage() {
        bt_play.setImageResource(R.drawable.ic_bt_play)
    }

    override fun setTimeZero(){
        timing.text = "00:00"
    }

    override fun getDuration() : Int {
        return trackDuration
    }

    override fun setCurrentTime() {
        timing.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerRepository.getCurrentPosition())
    }

    override fun getData() {
        var track = Gson().fromJson((intent.getStringExtra(MEDIA_KEY)), Track::class.java)
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_album_cover)))
            .into(image_album)
        track_name.text = track.trackName
        artist_name.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        album_name.text = track.collectionName
        year_release.text = track.releaseDate?.substring(0,4)
        genre.text = track.primaryGenreName
        country.text = track.country
        layout_media.visibility = View.VISIBLE
        url = track.previewUrl
    }

    override fun goBack() {
        finish()
    }

    override fun btPauseSetImage() {
        bt_play.setImageResource(R.drawable.ic_bt_pause)
    }
}
