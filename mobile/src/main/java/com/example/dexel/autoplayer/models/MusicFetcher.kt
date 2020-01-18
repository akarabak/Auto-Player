package com.example.dexel.autoplayer.models

import android.util.Log
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import com.thegrizzlylabs.sardineandroid.impl.SardineException
import kotlinx.coroutines.*

import java.io.File
import java.io.FileOutputStream

interface MusicFetcher {
    suspend fun listSongs(): List<Song>
    fun changeDirectory(newDirectory: File): Boolean
    fun playSong(song: Song): Deferred<String>
}

class LocalMusicFetcher(var currentDirectory: File): MusicFetcher{
    companion object {
        var TAG = LocalMusicFetcher::class.java.simpleName
        private val MEDIA_FILE = hashSetOf("mp3", "flac", "aac")
    }
    override fun changeDirectory(newDirectory: File): Boolean {
        return if (newDirectory.exists()) {
            currentDirectory = newDirectory
            true
        }
        else{
            false
        }
    }

    override suspend fun listSongs(): List<Song> = withContext(Dispatchers.IO) {
        Log.d(TAG, "listing songs in: $currentDirectory")
        val result = currentDirectory.listFiles().sorted()
                .filter {
                    val extension = it.path.split(".").last()
                    val isMedia =  MEDIA_FILE.contains(extension)
                    (it.isDirectory || isMedia)
                }.map {
                    Song(it.name, it.absolutePath, it.isDirectory)
                }
        Log.d(TAG, result.toString())
        result
    }

    override fun playSong(song: Song): Deferred<String> {
        return GlobalScope.async {
            song.path
        }
    }

}

class MusicFetcherNetwork(user: String, password: String, server: String): MusicFetcher {


    companion object {
        private val TAG = MusicFetcher::class.java.simpleName
    }

    private val sardine = OkHttpSardine()
    private var resource: String
    private var currentDirectory: File
    private var downloadDir: String = ""

    init {
        Log.i(TAG, "$user:$password")
        sardine.setCredentials(user, password)
        this.resource = server
        currentDirectory = File("/")
    }

    constructor(user: String, password: String, server: String, downloadDir: String): this(user, password, server){
        this.downloadDir = downloadDir
    }



    fun setCredentials(user: String, password: String){
        sardine.setCredentials(user, password)
    }

    fun setHost(host: String){
        resource = host
    }

    override fun changeDirectory(newDirectory: File): Boolean {
        Log.d(TAG, "changeDirectory: $newDirectory")
        try {
            if (sardine.exists("$resource$newDirectory")) {
                currentDirectory = newDirectory
            }
            else {
                return false
            }
        }
        catch (exception: SardineException){
            Log.e(TAG, exception.toString())
        }
        return true
    }

    override suspend fun listSongs(): List<Song> {
        var ret = listOf<Song>()
        try{
            val directory = "$resource$currentDirectory"
            Log.i(TAG, "listing songs in: $directory")
            val songs = sardine.list(directory)
            ret = songs.filter { "audio" in it.contentType || it.isDirectory }
                    .sortedBy { !it.isDirectory }
                    .map { Song(it.name, it.path, it.isDirectory) }
            Log.d(TAG, ret.toString())
        }
        catch (ex: SardineException){
            Log.e(TAG, ex.toString())
        }
        return ret
    }



    override fun playSong(song: Song) : Deferred<String> {
        return GlobalScope.async {
            val location = File(downloadDir, "temp.mp3")
            try {

                val writer = FileOutputStream(location)
                val input = sardine["$resource/${song.path}"]
                val buffer = ByteArray(4096)

                var read = input.read(buffer)
                while (read != -1) {
                    writer.write(buffer, 0, read)
                    read = input.read(buffer)
                }
                location.absolutePath
            } catch (ex: SardineException) {
                Log.e(TAG, ex.toString())
            }

            location.absolutePath
        }
    }
}