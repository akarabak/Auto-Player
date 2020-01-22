package com.example.dexel.autoplayer.di

import android.app.Activity
import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.viewModels.ViewModelBuilder
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [ViewModelBuilder::class, InterfaceMapperModule::class])
    internal abstract fun contributeMainActivity(): MainActivity

    @Binds
    @Named("mainActivity")
    internal abstract fun provideActivity(mainActivity: MainActivity): Activity
}
