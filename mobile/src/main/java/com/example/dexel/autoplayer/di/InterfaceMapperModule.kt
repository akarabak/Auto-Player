package com.example.dexel.autoplayer.di

import com.example.dexel.autoplayer.MainActivity
import com.example.dexel.autoplayer.models.ActivityDependent
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Scope

@Module
class InterfaceMapperModule  {
    @Provides
    @MainActivityScope
    fun activityDependent(mainActivity: MainActivity): ActivityDependent {
        return ActivityDependent(mainActivity)
    }
}

@Named("MainActivity")
annotation class MainActivityScope
