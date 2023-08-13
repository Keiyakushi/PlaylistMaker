package com.practicum.playlistmaker.playlist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistsBinding
import com.practicum.playlistmaker.playlist.data.PermissionStates
import com.practicum.playlistmaker.playlist.view_model.PlaylistViewModel
import com.practicum.playlistmaker.settings.domain.ISettingsInteractor
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistsFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistsBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val viewModel by viewModel<PlaylistViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCreatePlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPickMedia()
        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.permissionStateFlow.collect {
                when (it) {
                    PermissionStates.GRANTED -> {
                        pickMedia.launch(PickVisualMediaRequest
                            (ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    PermissionStates.DENIED_PERMANENTLY -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.fromParts("package", requireContext().packageName, null)
                        requireContext().startActivity(intent)
                    }
                }
            }
        }

        viewModel.allowedToBack.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            } else {
                showDialog()
            }
        }
    }

    private fun initListeners() {
        binding.createButton.isEnabled = false
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackPressed()
                }
            })
        binding.setImage.setOnClickListener {
            viewModel.onImageClicked()
        }
        binding.backIconMedia.setOnClickListener {
            viewModel.onBackPressed()
        }
        binding.namingEditText.doOnTextChanged { text, _, _, _ ->
            setColorStrokeNaming(text.toString())
            binding.createButton.isEnabled = !text.isNullOrEmpty()
            viewModel.savePlaylistName(text.toString())
        }
        binding.descriptionEditText.doOnTextChanged { text, start, before, count ->
            setColorStrokeDescription(text.toString())
            viewModel.savePlaylistDescription(text.toString())
        }
        binding.createButton.setOnClickListener {
            viewModel.onCreateBtClicked()
            findNavController().navigateUp()
            showSnackbar(binding.namingEditText.text.toString())
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.my_playlists)
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, uri.lastPathSegment ?: "image")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE, outputStream)

        viewModel.saveImageUri(file.toURI().toString())
    }

    private fun setColorStrokeNaming(text: String) {
        if (text.isNullOrEmpty()) {
            binding.namingLayout.defaultHintTextColor =
                ContextCompat.getColorStateList(requireContext(), R.color.input_text_layout)
            ContextCompat.getColorStateList(requireContext(), R.color.input_text_layout)
                ?.let { binding.namingLayout.setBoxStrokeColorStateList(it) }
        } else {
            binding.namingLayout.defaultHintTextColor =
                resources.getColorStateList(R.color.input_text_layout_blue, null)
            binding.namingLayout.setBoxStrokeColorStateList(
                resources.getColorStateList(R.color.input_text_layout_blue, null))
        }
    }

    private fun setColorStrokeDescription(text: String) {
        if (text.isNullOrEmpty()) {
            binding.descriptionLayout.defaultHintTextColor =
                ContextCompat.getColorStateList(requireContext(), R.color.input_text_layout)
            ContextCompat.getColorStateList(requireContext(), R.color.input_text_layout)
                ?.let { binding.descriptionLayout.setBoxStrokeColorStateList(it) }
        } else {
            binding.descriptionLayout.defaultHintTextColor =
                resources.getColorStateList(R.color.input_text_layout_blue, null)
            binding.descriptionLayout.setBoxStrokeColorStateList(
                resources.getColorStateList(R.color.input_text_layout_blue, null))
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.end_playlist_title))
            .setMessage(getString(R.string.playlist_message))
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(getString(R.string.end_playlist)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun initPickMedia() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                val cornerRadius =
                    requireContext().resources.getDimensionPixelSize(R.dimen.corner_album_cover)
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder_media)
                    .transform(RoundedCorners(cornerRadius))
                    .into(binding.setImage)
                saveImageToPrivateStorage(it)
            }
        }
    }

    private fun showSnackbar(playlistName: String) {
        val message =
            getString(R.string.playlist) + " " + playlistName + " " + getString(R.string.created)
        val theme = getKoin().get<ISettingsInteractor>().getThemeSettings().darkTheme
        if (!theme) {
            Snackbar
                .make(requireContext(),
                    binding.createPlaylistFragment,
                    message,
                    Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.icons))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .setDuration(MESSAGE_DURATION)
                .show()
        } else {
            Snackbar
                .make(requireContext(),
                    binding.createPlaylistFragment,
                    message,
                    Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.icons))
                .setDuration(MESSAGE_DURATION)
                .show()
        }
    }

    companion object {
        private const val QUALITY_IMAGE = 30
        private const val MESSAGE_DURATION = 4000
    }
}