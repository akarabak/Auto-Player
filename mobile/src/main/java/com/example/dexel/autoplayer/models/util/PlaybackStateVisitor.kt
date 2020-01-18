package com.example.dexel.autoplayer.models.util

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import java.lang.ref.WeakReference

class PlaybackStateVisitor(mediaSession: MediaSessionCompat) {
    private val mediaSession: WeakReference<MediaSessionCompat> = WeakReference(mediaSession)
    private val stateBuilder = Builder()
    fun onPause() {
        val state = stateBuilder
                .setState(STATE_PAUSED, 0, 0f)
                .setActions(ACTION_PLAY)
                .build()
        mediaSession.get()?.setPlaybackState(state)
    }
    fun onPlay() {
        val state = stateBuilder
                .setState(STATE_PLAYING, 0, 0f)
                .setActions(ACTION_PAUSE)
                .build()
        mediaSession.get()?.setPlaybackState(state)
    }
    fun onStop() {
        val state = stateBuilder
                .setState(STATE_STOPPED, 0, 0f)
                .setActions(ACTION_PLAY)
                .build()
        mediaSession.get()?.setPlaybackState(state)
    }
}
