package com.example.dexel.autoplayer.views

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.R
import com.example.dexel.autoplayer.viewModels.ViewModelFactory
import com.example.dexel.autoplayer.databinding.FragmentSongListBinding
import com.example.dexel.autoplayer.viewModels.MusicServiceViewModel
import java.util.*
import javax.inject.Inject

class SongFragment : BaseMusicFragment() {
    private var path: String? = null
    private var prevPaths = Stack<String?>()

    init {
        arguments?.apply {
            path = getString("path")
            Log.d(TAG, "path is $path")
        }
    }

    private lateinit var adapter: MySongRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    companion object {
        val TAG = SongFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) { //gets called once when created
        Log.d(TAG, "onCreate")
        arguments?.apply {
            prevPaths.add(path)
            path = getString("path")
            Log.d(TAG, "path is $path")
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { //every time when fragment poped from stack, rotated, etc.
        val binding: FragmentSongListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_list, container, false)
        Log.i(TAG, "onCreateView")
        Log.d(TAG, "ViewModel $viewModel")

        viewModel.path = path

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        adapter = MySongRecyclerViewAdapter(object: MySongRecyclerViewAdapter.OnClickListener {
            override fun onClick(mediaItem: MediaBrowserCompat.MediaItem) {
                viewModel.playSong(mediaItem)
            }
        })
        recyclerView = binding.root.findViewById(R.id.song_list)
        Log.i(TAG, "assigning adapter and layout")
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        Log.d(TAG, activity.toString())
        activity?.run {
            viewModel.connect(this)
        }

        viewModel.songs.observe(this, Observer<List<MediaBrowserCompat.MediaItem>> { songs ->
            if (songs != null) {
                Log.i(TAG, "setting new songs")
                adapter.setValues(songs)
            } else{
                Log.e(TAG, "list is null")
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        viewModel.disconnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}
