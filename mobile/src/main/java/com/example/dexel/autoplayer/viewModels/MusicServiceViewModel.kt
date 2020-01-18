package com.example.dexel.autoplayer.viewModels

import android.app.Activity
import android.app.Application
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.models.MediaBrowserWrapper
import com.example.dexel.autoplayer.models.MediaScanner
import com.example.dexel.autoplayer.models.MusicFetcher
import com.example.dexel.autoplayer.views.PlaybackFragment
import com.example.dexel.autoplayer.views.SongFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MusicServiceViewModel @Inject constructor(
    application: Application,
    private val mediaBrowserWrapper: MediaBrowserWrapper,
    private val mediaScanner: MediaScanner
): AndroidViewModel(application), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private lateinit var fetcher: MusicFetcher
    private val _songs: MutableLiveData<PagedList<MediaBrowserCompat.MediaItem>> = MutableLiveData()
    val songs: LiveData<PagedList<MediaBrowserCompat.MediaItem>> = _songs

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val cover: MutableLiveData<Drawable> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val artist: MutableLiveData<String> = MutableLiveData()
    val album: MutableLiveData<String> = MutableLiveData()

    var path: String? = null

    private var mediaController: MediaControllerCompat? = null

    private var artistCursor: MediaScanner.ArtistCursor? = null

    init {
//        launch {
//            artistCursor = mediaScanner.scanArtists()
//        }
    }

    companion object {
        private val TAG = MusicServiceViewModel::class.java.simpleName
    }

    fun connect(activity: Activity) {
        mediaBrowserWrapper.connect(activity, path)
        activity.volumeControlStream = AudioManager.STREAM_MUSIC

        mediaController = MediaControllerCompat.getMediaController(activity)

        //TODO observerForever?
        mediaBrowserWrapper.songs.observeForever {
            _songs.value = it
        }

//        launch {
//            Log.d(TAG, "Artists: ${artistCursor?.nextFifty()}")
//        }

    }

    fun disconnect() {
        mediaController = null
        mediaBrowserWrapper.disconnect()
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
        disconnect()
    }


    fun updateSongs() {
//        Log.i(TAG, "updateSongs")
//        if (mediaBrowser == null) {
//            Log.i(TAG, "null for some reason")
//        }
//        mediaBrowser?.let {
//            Log.i(TAG, it.root)
//            it.subscribe(it.root, object : MediaBrowserCompat.SubscriptionCallback() {
//                override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
//                    songs.postValue(children.map { Song(it.mediaId!!, it.description.toString(), it.isBrowsable) })
//                    super.onChildrenLoaded(parentId, children)
//                }
//            })
//        }
    }

    val playSong = MutableLiveData<Fragment>()

    fun playSong(mediaItem: MediaBrowserCompat.MediaItem) {
        Log.d(TAG, "playSong: $mediaItem")
        if (mediaItem.isBrowsable) {
            //mediaBrowser.subscribe(mediaItem.mediaId!!, subscriptionCallback)
            val fragment = SongFragment()
            val bundle = Bundle()
            bundle.putString("path", mediaItem.mediaId!!)
            fragment.arguments = bundle
            playSong.value = fragment
        }
        else {
            // play
            val transportControls = mediaController?.transportControls
            transportControls?.playFromMediaId(mediaItem.mediaId, null)
            Log.d(TAG, mediaController?.playbackState.toString())
            playSong.value = PlaybackFragment()

        }

    }

    fun playPause() {
        Log.d(TAG, "playPause")
        val transportControls = mediaController?.transportControls
        when (mediaController?.playbackState?.state) {
            PlaybackStateCompat.STATE_PAUSED -> transportControls?.play()
            PlaybackStateCompat.STATE_PLAYING -> transportControls?.pause()
        }
    }
}
