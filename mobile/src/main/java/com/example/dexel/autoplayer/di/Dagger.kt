package com.example.dexel.autoplayer.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.MainApplication
import com.example.dexel.autoplayer.services.MyMusicService
import com.example.dexel.autoplayer.viewModels.MusicServiceViewModel
import com.example.dexel.autoplayer.viewModels.ViewModelKey
import dagger.*
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.multibindings.IntoMap
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityModule::class,
    ServiceModule::class,
    FragmentModule::class,
    AppModule::class
])
interface AppComponent: AndroidInjector<MainApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}

@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun contributesService(): MyMusicService
}

@Module
abstract class AppModule {
    @Binds
    abstract fun applicationContext(application: Application): Context
}

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MusicServiceViewModel::class)
    abstract fun bindViewModel(viewmodel: MusicServiceViewModel): ViewModel
}
