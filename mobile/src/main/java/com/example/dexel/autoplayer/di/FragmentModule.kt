package com.example.dexel.autoplayer.di

import com.example.dexel.autoplayer.viewModels.ViewModelBuilder
import com.example.dexel.autoplayer.views.BaseMusicFragment
import com.example.dexel.autoplayer.views.PlaybackFragment
import com.example.dexel.autoplayer.views.SongFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    internal abstract fun providesBaseMusicFragment(): BaseMusicFragment

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    internal abstract fun providesSongFragment(): SongFragment

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    internal abstract fun providesPlaybackFragment(): PlaybackFragment
}
