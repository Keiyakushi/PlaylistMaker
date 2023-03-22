package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class MediaActivity : AppCompatActivity() {
    companion object{
        const val MEDIA_KEY = "MEDIA_KEY"
        const val REFRESH_MILLIS = 300L
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }
    lateinit var bt_play: ImageButton
    lateinit var timing: TextView
    var playerState = STATE_DEFAULT
    val mediaPlayer = MediaPlayer()
    var mainThreadHandler: Handler? = null
    var trackDuration : Int?= 30
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val layout_media = findViewById<ConstraintLayout>(R.id.layout_media)
        val back_icon = findViewById<ImageView>(R.id.back_icon_media)
        val image_album = findViewById<ImageView>(R.id.cover_media)
        val track_name = findViewById<TextView>(R.id.track_name_media)
        val artist_name = findViewById<TextView>(R.id.artist_name_media)
        val bt_add_playlist = findViewById<ImageButton>(R.id.bt_add_to_playlist)
        bt_play = findViewById<ImageButton>(R.id.bt_play)
        val bt_follow = findViewById<ImageButton>(R.id.bt_follow)
        timing = findViewById<TextView>(R.id.timing)
        val duration = findViewById<TextView>(R.id.time_duration)
        val album_name = findViewById<TextView>(R.id.album_name)
        val year_release = findViewById<TextView>(R.id.year_score)
        val genre = findViewById<TextView>(R.id.genre_name)
        val country = findViewById<TextView>(R.id.country_name)
        mainThreadHandler = Handler(Looper.getMainLooper())
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
        var url : String? = track.previewUrl

        if (url!=null) {
            mediaPlayer.setDataSource(url)
            preparePlayer()
        }
        bt_play.setOnClickListener {
            playbackControl()
        }
        back_icon.setOnClickListener{
            finish()
        }
    }
    fun preparePlayer() {
        var theme = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(
            SWITCH_PREFERENCES_KEY, false
        )
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            bt_play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            if (theme){
                bt_play.setImageResource(R.drawable.ic_bt_play_dark)
            } else bt_play.setImageResource(R.drawable.ic_bt_play)
            mainThreadHandler?.removeCallbacksAndMessages(null)
            playerState = STATE_PREPARED
            timing.text = "00:00"
        }
    }
    fun startPlayer() {
        var theme = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(
            SWITCH_PREFERENCES_KEY, false
        )
        mediaPlayer.start()
        if (theme){
            bt_play.setImageResource(R.drawable.ic_bt_pause_dark)
        } else bt_play.setImageResource(R.drawable.ic_bt_pause)
        startTimer()
        playerState = STATE_PLAYING
    }
    fun pausePlayer() {
        var theme = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getBoolean(
            SWITCH_PREFERENCES_KEY, false
        )
        mediaPlayer.pause()
        if (theme){
            bt_play.setImageResource(R.drawable.ic_bt_play_dark)
        } else bt_play.setImageResource(R.drawable.ic_bt_play)
        mainThreadHandler?.removeCallbacks {updateTimer()}
        playerState = STATE_PAUSED
    }
    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    fun startTimer() {
        mainThreadHandler?.post(
            updateTimer()
        )
    }
    fun updateTimer() : Runnable{
        return object : Runnable{
            override fun run() {
                val elapsedTime = mediaPlayer.currentPosition
                val remainingTime = 30000 - elapsedTime
                if(remainingTime > 0){
                    timing.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(this, REFRESH_MILLIS)
                } else {
                    timing.text = "00:00"
                }
                }
            }
        }
    }
