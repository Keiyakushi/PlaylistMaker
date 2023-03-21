package com.practicum.playlistmaker

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
import java.text.SimpleDateFormat
import java.util.*

class MediaActivity : AppCompatActivity() {
    companion object{
        const val MEDIA_KEY = "MEDIA_KEY"
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
        val bt_play = findViewById<ImageButton>(R.id.bt_play)
        val bt_follow = findViewById<ImageButton>(R.id.bt_follow)
        val timing = findViewById<TextView>(R.id.timing)
        val duration = findViewById<TextView>(R.id.time_duration)
        val album_name = findViewById<TextView>(R.id.album_name)
        val year_release = findViewById<TextView>(R.id.year_score)
        val genre = findViewById<TextView>(R.id.genre_name)
        val country = findViewById<TextView>(R.id.country_name)


        var track : Track= Gson().fromJson((intent.getStringExtra(MEDIA_KEY)),Track::class.java)

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder_media)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_dp)))
            .into(image_album)
        track_name.text = track.trackName
        artist_name.text = track.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
        album_name.text = track.collectionName
        year_release.text = track.releaseDate.substring(0,4)
        genre.text = track.primaryGenreName
        country.text = track.country
        layout_media.visibility = View.VISIBLE

        back_icon.setOnClickListener{
            finish()
        }
    }
}