package com.practicum.playlistmaker.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistsBinding

class CreatePlaylistsFragment : Fragment(){
    private lateinit var binding: FragmentCreatePlaylistsBinding
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreatePlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createButton.isEnabled = false
        initPickMedia()
        binding.setImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }
    private fun initPickMedia(){
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            if(it != null){
                val cornerRadius = requireContext().resources.getDimensionPixelSize(R.dimen.corner_album_cover)
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder_media)
                    .transform(RoundedCorners(cornerRadius))
                    .into(binding.setImage)
            }
        }
    }
}