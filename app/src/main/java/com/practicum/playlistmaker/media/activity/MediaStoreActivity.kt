package com.practicum.playlistmaker.media.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.databinding.ActivityMediaStoreTabBinding
import com.practicum.playlistmaker.databinding.ActivitySearchBinding

class MediaStoreActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMediaStoreTabBinding.inflate(layoutInflater) }
    private lateinit var tabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_follow_tracks)
        setContentView(binding.root)
        binding.viewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorite_tracks)
                else -> getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        binding.backIcon.setOnClickListener(){
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}