package com.example.dexel.autoplayer.di

import android.app.Application
import android.content.Context
import com.example.dexel.autoplayer.MainApplication
import com.example.dexel.autoplayer.services.MyMusicService
import dagger.*
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
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

