package com.practicum.playlistmaker.ui.root.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.themeSwitcherStateLiveData.observe(viewLifecycleOwner) { isChecked ->
            binding.switchThemeMode.isChecked = isChecked
        }
        binding.switchThemeMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitcherClicked(isChecked)
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