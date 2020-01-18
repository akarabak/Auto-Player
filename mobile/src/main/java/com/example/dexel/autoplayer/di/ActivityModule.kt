package com.example.dexel.autoplayer.di

import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.viewModels.ViewModelBuilder
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [ViewModelBuilder::class, InterfaceMapperModule::class, ViewModelModule::class])
    internal abstract fun contributeMainActivity(): MainActivity
}
