package com.example.dexel.autoplayer.models.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import java.net.URI

fun retrieveMetadata(path: String): MediaMetadataCompat {
    val bitmap = getBitmap(path)

    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    val title: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
    val artist: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    val album: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)

    return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            .build()
}
fun metadataToDetails(metadata:  MediaMetadataCompat): SongDetails {
    metadata.apply {
        return SongDetails(getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                getString(MediaMetadataCompat.METADATA_KEY_ARTIST),
                getString(MediaMetadataCompat.METADATA_KEY_ALBUM),
                getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
    }
}

data class SongDetails(val title: String, val artist: String, val album: String, val bitmap: Bitmap)

fun getBitmap(path: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)

    val imageByteArray = retriever.embeddedPicture
    imageByteArray?.let {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }
    return null
}
