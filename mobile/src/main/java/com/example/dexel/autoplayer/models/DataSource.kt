package com.example.dexel.autoplayer.models

import android.media.MediaDataSource
import android.os.Handler
import android.os.HandlerThread
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.io.InputStream

class DataSource() : MediaDataSource() {
    companion object {
        private val TAG = MusicFetcher::class.java.simpleName
    }
    private val sardine: Sardine
    private val resource = ""
    private var thread: HandlerThread
    private var handler: Handler
    var stream: InputStream? = null


    init {

        sardine = OkHttpSardine()
        //sardine.setCredentials(user, password)
        thread = HandlerThread(TAG)
        thread.start()
        handler = Handler(thread.looper)
    }




    override fun readAt(position: Long, buffer: ByteArray?, offset: Int, size: Int): Int {
        stream?.read(buffer, offset, size)
        return 0
    }

    override fun getSize(): Long {
        return 0
    }

    override fun close() {
        stream?.close()
    }



}