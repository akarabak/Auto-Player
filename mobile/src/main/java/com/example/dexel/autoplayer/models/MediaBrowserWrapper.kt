package com.example.dexel.autoplayer.models

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.dexel.autoplayer.models.util.metadataToDetails
import com.example.dexel.autoplayer.services.MyMusicService
import javax.inject.Inject
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.example.dexel.autoplayer.MainActivity

class MediaBrowserWrapper @Inject constructor(
        private val application: Application
) {
    private var currentPath: String? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private val mediaBrowser: MediaBrowserCompat

    private val _ready = MutableLiveData<MediaControllerCompat>()
    val ready: LiveData<MediaControllerCompat> = _ready

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val cover: MutableLiveData<Drawable> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val artist: MutableLiveData<String> = MutableLiveData()
    val album: MutableLiveData<String> = MutableLiveData()

    val songs: MutableLiveData<PagedList<MediaBrowserCompat.MediaItem>> = MutableLiveData()

    private var runningActivity: Activity? = null

    init {
        mediaBrowser = MediaBrowserCompat(application, ComponentName(application, MyMusicService::class.java),
                ConnectionCallback(), null)
    }

    fun connect(activity: Activity, path: String?) {
        currentPath = path
        runningActivity = activity
        mediaBrowser.connect()
    }

    fun disconnect() {
        runningActivity?.let {
            MediaControllerCompat.getMediaController(it).unregisterCallback(mediaControllerCallback)
        }
        runningActivity = null
        mediaBrowser.disconnect()
    }


    private inner class ConnectionCallback: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {

            Log.i(TAG, "connecting")
            mediaBrowser.sessionToken.let {
                val mediaController = MediaControllerCompat(application, it)
                mediaController.registerCallback(mediaControllerCallback)

                runningActivity?.apply {
                    MediaControllerCompat.setMediaController(this, mediaController)
                }

                _ready.value = mediaController


                Log.i(TAG, "root is ${mediaBrowser.root}")

                currentPath.apply {
                    if (this == null) {
                        Log.d(TAG, "path is null")
                        val bundle = Bundle()
                        bundle.putInt(MediaBrowserCompat.EXTRA_PAGE, 0)
                        bundle.putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, 10)
                        mediaBrowser.subscribe(mediaBrowser.root, bundle,
                            subscriptionCallback)
                    } else {
                        Log.d(TAG, "path is $currentPath")
                        mediaBrowser.subscribe(this, subscriptionCallback)
                    }
                }

            }
        }
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.d(MediaBrowserWrapper.TAG, "onMetadataChanged")
            metadata?.let {
                val details = metadataToDetails(it)
                title.value = details.title
                artist.value = details.artist
                album.value = details.album
                cover.value = BitmapDrawable(application.resources, details.bitmap)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
        }
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>, options: Bundle) {
            Log.d(TAG, "options $options")
            //songs.value = LivePagedListBuilder(datasourceFactory, 0)
        }
    }

    class MediaDataSource: PositionalDataSource<MediaBrowser.MediaItem>() {
        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MediaBrowser.MediaItem>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<MediaBrowser.MediaItem>) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

//    class DatasourceFactory @Inject constructor(
//        private val mediaBrowserWrapper: MediaBrowserWrapper
//    ): DataSource.Factory<Integer, MediaBrowserCompat.MediaItem>() {
//        override fun create(): DataSource<Integer, MediaBrowserCompat.MediaItem> {
//
//        }
//
//    }

    companion object {
        private val TAG = MediaBrowserWrapper::class.simpleName
    }
}
