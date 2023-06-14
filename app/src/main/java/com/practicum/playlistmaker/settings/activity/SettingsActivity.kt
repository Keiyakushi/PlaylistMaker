package com.practicum.playlistmaker.settings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.themeSwitcherStateLiveData.observe(this) { isChecked ->
            binding.switchThemeMode.isChecked = isChecked
        }
        binding.switchThemeMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
        }
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.settingsShare.setOnClickListener {
            viewModel.onShareAppClicked()
        }
        binding.settingsSuport.setOnClickListener {
            viewModel.onSupportClicked()
        }
        binding.userAgreement.setOnClickListener {
            viewModel.onUserAgreement()
        }
    }
}