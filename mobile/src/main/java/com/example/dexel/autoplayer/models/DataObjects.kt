package com.example.dexel.autoplayer.models

import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.util.Log
import com.example.dexel.autoplayer.models.util.getBitmap


data class Song(val name: String, val path: String, val directory: Boolean) {
    override fun toString(): String = name

    fun getMediaItem(): MediaItem {
        return when(directory) {
            true -> MediaItem(
                    MediaDescriptionCompat.Builder()
                            .setTitle(name)
                            .setMediaId(path)
                            .build(),
                    MediaItem.FLAG_BROWSABLE)
            false -> MediaItem(
                    MediaDescriptionCompat.Builder()
                            .setTitle(name)
                            .setMediaId(path)
                            .setIconBitmap(getBitmap(path))
                            .build(),
                    MediaItem.FLAG_PLAYABLE)
        }
    }
}

