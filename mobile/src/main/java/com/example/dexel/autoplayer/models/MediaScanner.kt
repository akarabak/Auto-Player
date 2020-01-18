package com.example.dexel.autoplayer.models

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.util.Log
import com.example.dexel.autoplayer.models.util.getBitmap
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MediaScanner @Inject constructor(private val applicationContext: Context) {
//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.IO + job

    private val projections = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.YEAR
    )

    private val selection = "${MediaStore.Audio.Media.YEAR} >= ?"
    private val sortOrder = "${MediaStore.Audio.Media.TRACK} ASC"

    private val contentResolver = applicationContext.contentResolver

    private fun query(projections: Array<String>, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): android.database.Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projections,
            selection,
            selectionArgs,
            sortOrder
        )
    }

    suspend fun scanMusic() = coroutineScope  {
        launch {
            val cursor = async {
                query(
                    projections,
                    selection,
                    arrayOf("2000"),
                    sortOrder
                )
            }.await()
            cursor?.run {
                val idColumn = getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val albumColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val trackColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val titleColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val yearColumn = getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                while (moveToNext()) {
                    val id = getLong(idColumn)
                    val title = getString(titleColumn)
                    val album = getString(albumColumn)
                    val year = getInt(yearColumn)
                    val track = getString(trackColumn)
                    val artist = getString(artistColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                    )

                    Log.d(TAG, "$title, $album, $year, $track, $artist $id")
                }
            }
        }
    }

    @Throws(ScanFailedException::class)
    suspend fun scanArtists(): ArtistCursor = coroutineScope{
        val artists = LinkedList<MediaBrowserCompat.MediaItem>()
        val cursor = query(
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.MediaColumns.DATA
            ),
            null, null, null
        )
        if (cursor == null) {
            throw ScanFailedException
        }
        else {
            ArtistCursor(cursor)
        }
    }

    object ScanFailedException : Exception()

    class ArtistCursor(private val cursor: Cursor) {
        private val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        private val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        private val filePathColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
        
        fun nextFifty(): List<MediaBrowserCompat.MediaItem> {
            return nextN(50)
        }

        fun nextN(n: Int = -1): List<MediaBrowserCompat.MediaItem> {
            val artists = mutableListOf<MediaBrowserCompat.MediaItem>()
            while (cursor.moveToNext()) {
                artists += buildMediaItem(cursor)
                if (n != -1 && artists.size >= n) {
                    return artists
                }
            }
            return artists
        }

        private fun buildMediaItem(cursor: Cursor): MediaBrowserCompat.MediaItem {
            cursor.run {
                val id = getLong(idColumn)
                val artist = getString(artistColumn)
                val filePath = getString(filePathColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )

                val item = MediaBrowserCompat.MediaItem(
                    MediaDescriptionCompat.Builder()
                        .setMediaUri(contentUri)
                        .setMediaId(filePath)
                        .setTitle(artist)
                        .setIconBitmap(getBitmap(filePath))
                        .build(),
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                )
                return item
            }
        }
    }

    companion object {
        private val TAG = MediaScanner::class.simpleName
    }
}
