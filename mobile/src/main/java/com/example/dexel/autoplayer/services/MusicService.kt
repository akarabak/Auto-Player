package com.example.dexel.autoplayer.services

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.dexel.autoplayer.R
import com.example.dexel.autoplayer.models.*
import com.example.dexel.autoplayer.models.util.PlaybackStateVisitor
import com.example.dexel.autoplayer.models.util.retrieveMetadata
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 *
 *
 * To implement a MediaBrowserService, you need to:
 *
 *  *  Extend [MediaBrowserServiceCompat], implementing the media browsing
 * related methods [MediaBrowserServiceCompat.onGetRoot] and
 * [MediaBrowserServiceCompat.onLoadChildren];
 *
 *  *  In onCreate, start a new [MediaSessionCompat] and notify its parent
 * with the session's token [MediaBrowserServiceCompat.setSessionToken];
 *
 *  *  Set a mediaSessionCallback on the [MediaSessionCompat.setCallback].
 * The mediaSessionCallback will receive all the user's actions, like play, pause, etc;
 *
 *  *  Handle all the actual music playing using any method your app prefers (for example,
 * [android.media.MediaPlayer])
 *
 *  *  Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * [MediaSessionCompat.setPlaybackState]
 * [MediaSessionCompat.setMetadata] and
 * [MediaSessionCompat.setQueue])
 *
 *  *  Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 *
 * To make your app compatible with Android Auto, you also need to:
 *
 *  *  Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 *
 */

class MyMusicService : MediaBrowserServiceCompat(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    companion object {
        private val TAG = MyMusicService::class.simpleName
    }

    @Inject
    lateinit var mediaScanner: MediaScanner

    private lateinit var musicFetcher: MusicFetcher
    private val ROOT_ID = "com.example.dexel.autoplayer.models.ROOT_ID"

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var baseDirectory: String
    private var isLocal: Boolean = true

    inner class MediaSessionCallback(private val mediaSession: MediaSessionCompat): MediaSessionCompat.Callback() {
        private var currentlyPlayingMedia: String? = null
        private val playbackStateVisitor = PlaybackStateVisitor(mediaSession)
        private val player = MediaPlayer()

        override fun onPrepare() {
            Log.d(TAG, "onPrepare: $currentlyPlayingMedia")
            val audioManager = getSystemService(AudioManager::class.java) as AudioManager
            val builder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setOnAudioFocusChangeListener {
                when(it) {
                    AudioManager.AUDIOFOCUS_LOSS -> onPause()
                    AudioManager.AUDIOFOCUS_GAIN -> onPause()
                }
            }

            val requestStatus = audioManager.requestAudioFocus(builder.build())
            Log.d(TAG, "Request: $requestStatus")
            when (requestStatus) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    if (!mediaSession.isActive) {
                        mediaSession.isActive= true

                    }

                    currentlyPlayingMedia?.let{
                        Log.d(TAG, "setting metadata")
                        val metadata = retrieveMetadata(it)
                        mediaSession.setMetadata(metadata)
                        playbackStateVisitor.onPlay()

                        player.setDataSource(it)
                        player.prepare()
                        player.start()


                    }
                }
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {

                }
                else -> {

                }
            }
        }

        override fun onPlay() { // more like resume
            Log.d(TAG, "onPlay")
            player.start()

            playbackStateVisitor.onPlay()
        }



        override fun onStop() {
            Log.d(TAG, "onStop")
            player.stop()
            mediaSession.isActive = false
            playbackStateVisitor.onStop()
        }

        override fun onPause() {
            Log.d(TAG, "onPause")
            player.pause()

            playbackStateVisitor.onPause()
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
            Log.d(TAG, "onPlayFromMediaId: $mediaId")
            player.reset()
            player.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())

            launch {
                val source: String = if (isLocal){
                    mediaId
                }
                else{
                    musicFetcher.playSong(Song("", mediaId, false)).await()
                }
                Log.d(TAG, "File to be played: $source")
                currentlyPlayingMedia = source
                onPrepare()
            }
        }

        override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
//            contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//
//
//            )
        }

        override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
            super.onPrepareFromUri(uri, extras)
        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        Log.d(TAG, "onCreate")

        setupMusicFetcher()

        mediaSession = MediaSessionCompat(this, TAG)
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        // allows starting to play the song
        val initialPlaybackState = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
                .build()

        mediaSession.setPlaybackState(initialPlaybackState)
        mediaSession.setCallback(MediaSessionCallback(mediaSession)) //TODO memory leak???

        sessionToken = mediaSession.sessionToken

    }

    private fun setupMusicFetcher() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val username = sharedPreference.getString(getString(R.string.username), "")
        val password = sharedPreference.getString(getString(R.string.password), "")
        val server = sharedPreference.getString(getString(R.string.server), "")



        musicFetcher = if (username != "" && password != "" && server != "") {
            val uri = Uri.parse(server)
            val host = uri.buildUpon().path("").toString()
            baseDirectory = uri.path
            Log.i(TAG, "Connecting to: $host")
            isLocal = false
            MusicFetcherNetwork(username, password, host, Environment.getExternalStorageDirectory().absolutePath)

        }
        else {
            Log.e(TAG, "credentials failed to set. Using local")
            baseDirectory = Environment.getExternalStorageDirectory().absolutePath
            isLocal = true
            LocalMusicFetcher(Environment.getExternalStorageDirectory())
        }
    }

    override fun onDestroy() {
        mediaSession.isActive = false
        mediaSession.release()
        job.cancel()
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>, options: Bundle) {

        val page = options.getInt(MediaBrowserCompat.EXTRA_PAGE)
        val pageSize = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE)

        Log.d(TAG, "onLoadChildren: $parentId with page $page and size $pageSize")
        result.detach()
        if (parentId == ROOT_ID) {
            launch {
                val artists = mediaScanner.scanArtists()
                result.sendResult(artists.nextN(50))
            }
        }
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>) {
        Log.d(TAG, "onLoadChildren: $parentId")
        result.detach()
        if (parentId == ROOT_ID) {
            launch {
                val artists = mediaScanner.scanArtists()
                result.sendResult(artists.nextN(150))
            }

//            val localItem = MediaItem(MediaDescriptionCompat.Builder()
//                    .setTitle("Local Media")
//                    .setDescription("Local Media")
//                    .setMediaId(baseDirectory)
//                    .build(),
//                    MediaItem.FLAG_BROWSABLE)
//            result.sendResult(mutableListOf(localItem))
        }
        else {
            result.detach()
            launch {
                if (musicFetcher.changeDirectory(File(parentId))) {
                    Log.d(TAG, "directory changed")
                }
                else {
                    Log.e(TAG, "failed to change directory")
                }
                result.sendResult(musicFetcher.listSongs()
                        .map {
                            it.getMediaItem()
                        } as MutableList<MediaItem>)
            }
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        Log.d(TAG, "onGetRoot")
        return BrowserRoot(ROOT_ID, null)
    }

}
