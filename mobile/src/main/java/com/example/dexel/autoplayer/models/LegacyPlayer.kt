package com.example.dexel.autoplayer.models

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.example.dexel.autoplayer.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

//class LegacyPlayer: Service() {
//    private lateinit var fetcher: MusicFetcher
//
//    override fun onBind(intent: Intent?): IBinder {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
//        val username = sharedPreference.getString(getString(R.string.username), "")
//        val password = sharedPreference.getString(getString(R.string.password), "")
//        val server = sharedPreference.getString(getString(R.string.server), "")
//
//        val uri = Uri.parse(server)
//        val host = uri.buildUpon().path("").build().toString()
//
//        if (username != "" && password != "" && server != ""){
//            fetcher = MusicFetcherNetwork(username, password, host, cacheDir.absolutePath)
//            onHandleIntent(intent)
//        }
//        else{
//            Log.e(TAG, "username, password, or server is not set")
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private var player: MediaPlayer = MediaPlayer()
//    private var isPlaying = false
//    enum class STATES {
//        PLAY,
//        PAUSE,
//        STOP
//    }
//
//    companion object {
//        const val SONG = "SONG"
//        const val COMMAND = "COMMAND"
//        private val TAG = LegacyPlayer::class.java.simpleName
//    }
//
//    private fun play(file: String){
//        Log.d(TAG, "play")
//        player.reset()
//        player.setAudioAttributes(AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
//        val file = fetcher.playSong(file)
//        GlobalScope.launch{
//            try {
//                val song = file.await()
//                notifyFileReady(song)
//                player.setDataSource(song)
//
//                player.prepare()
//                player.start()
//                isPlaying = true
//            }
//            catch (e: IOException){
//                Log.e(TAG, e.toString())
//            }
//        }
//
//    }
//
//    private fun notifyFileReady(file: String){
//        val intent = Intent("SET_COVER")
//        intent.putExtra("location", file)
//        sendBroadcast(intent)
//    }
//
//    private fun stop(){
//        Log.d(TAG, "stop")
//        player.stop()
//        isPlaying = false
//    }
//
//    private fun pause(){
//        Log.d(TAG, "pause")
//        when(isPlaying){
//            true -> player.stop()
//            false -> player.start()
//        }
//        isPlaying = !isPlaying
//    }
//
//
//    private fun onHandleIntent(intent: Intent?) {
//        Log.i(TAG, "onHandleIntent")
//        intent?.run {
//            val action = getSerializableExtra(COMMAND)
//            if (action is STATES){
//                when(action){
//                    STATES.PLAY -> play(getStringExtra(SONG))
//                    STATES.PAUSE -> pause()
//                    STATES.STOP -> stop()
//                }
//            }
//        }
//    }
//}