package com.example.dexel.autoplayer

import android.support.test.runner.AndroidJUnit4
import com.example.dexel.autoplayer.services.MyMusicService
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyMusicServiceTest {
    val CUT = MyMusicService()

    @Test
    fun testGetRoot() {
        var clientPackageName = ""
        val browserRoot = CUT.onGetRoot(clientPackageName, 0, null)
        assertEquals("", browserRoot.toString())
    }
}
