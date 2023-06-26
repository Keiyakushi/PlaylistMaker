package com.practicum.playlistmaker.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.main.model.MainStates
import com.practicum.playlistmaker.main.view_model.MainViewModel
import com.practicum.playlistmaker.media.activity.MediaStore
import com.practicum.playlistmaker.search.activity.SearchActivity
import com.practicum.playlistmaker.settings.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.contentStateLiveData.observe(this) {
            when (it) {
                MainStates.Library -> {
                    val settingsIntent = Intent(this, MediaStore::class.java)
                    startActivity(settingsIntent)
                }
                MainStates.Search -> {
                    val searchIntent = Intent(this, SearchActivity::class.java)
                    startActivity(searchIntent)
                }
                MainStates.Settings -> {
                    val settingsIntent = Intent(this, SettingsActivity::class.java)
                    startActivity(settingsIntent)
                }
            }
        }
        binding.search.setOnClickListener {
            viewModel.onSearchButtonClicked()
        }
        binding.media.setOnClickListener {
            viewModel.onLibraryButtonClicked()
        }
        binding.setting.setOnClickListener {
            viewModel.onSettingsButtonClicked()
        }

    }
}