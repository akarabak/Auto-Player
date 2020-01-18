package com.example.dexel.autoplayer

import com.example.dexel.autoplayer.models.LocalMusicFetcher
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class LocalMusicFetcherTest {
    lateinit var CUT: LocalMusicFetcher

    @Before
    fun setUp() {
        CUT = LocalMusicFetcher(File("."))
    }

    @Test
    fun listSongsTest() {
        val result = CUT.listSongs()
        assertTrue(result.isNotEmpty())
    }


    @Test
    fun listSongsInParentDirectory() {
        CUT.changeDirectory(File(".").absoluteFile.parentFile)
        assertTrue(CUT.listSongs().isNotEmpty())
    }

            @Test
    fun changeDirectorySuccessfullyTest() {
        assertTrue(CUT.changeDirectory(File(".")))
    }

    @Test
    fun changeDirectoryFailedTest() {
        assertFalse(CUT.changeDirectory(File("sdfhgvbsdffasdfas")))
    }

}