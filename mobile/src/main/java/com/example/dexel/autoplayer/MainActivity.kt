package com.example.dexel.autoplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dexel.autoplayer.di.MainActivityScope
import com.example.dexel.autoplayer.models.ActivityDependent
import com.example.dexel.autoplayer.viewModels.MusicServiceViewModel
import com.example.dexel.autoplayer.views.Settings
import com.example.dexel.autoplayer.views.SongFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import javax.inject.Named


class MainActivity : DaggerAppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    @MainActivityScope
    lateinit var activityDependent: ActivityDependent

    private val viewModel by viewModels<MusicServiceViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)

        }
        super.onCreate(savedInstanceState)




        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        //viewModel = viewModels<MusicServiceViewModel> { factory }.value
        Log.d(TAG, "ViewModel $viewModel")
        viewModel.playSong.observe(this, Observer<Fragment> {
            Log.d(TAG, "switching fragment")
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.main_fragment, it)
                    .addToBackStack(null)
                    .commit()
        })

        if (savedInstanceState != null){
            Log.d(TAG, "${fragmentManager.fragments} and size is ${fragmentManager.fragments.size}")
            return
        }



        //fragmentManager.beginTransaction().add(R.id.main_fragment, DataEntry()).commit()

        fragmentManager.beginTransaction().add(R.id.main_fragment, SongFragment()).commit()
        Log.i(TAG, "end of onCreate")

        //MediaBrowser(this, LegacyMusicService::class.java, ).connect()
}

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    fun switchTo(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.replace(R.id.main_fragment, fragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.settings -> switchTo(Settings())
        }
        return super.onOptionsItemSelected(item)
    }
}
