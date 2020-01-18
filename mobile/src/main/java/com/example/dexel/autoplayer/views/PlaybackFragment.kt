package com.example.dexel.autoplayer.views

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

import com.example.dexel.autoplayer.R
import com.example.dexel.autoplayer.viewModels.ViewModelFactory
import com.example.dexel.autoplayer.databinding.FragmentPlaybackBinding
import com.example.dexel.autoplayer.viewModels.MusicServiceViewModel
import javax.inject.Inject


class PlaybackFragment : BaseMusicFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var binding: FragmentPlaybackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_playback, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        binding.invalidateAll()

        Log.d(TAG, "onCreateView")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {

        private val TAG = PlaybackFragment::class.java.simpleName
    }
}
