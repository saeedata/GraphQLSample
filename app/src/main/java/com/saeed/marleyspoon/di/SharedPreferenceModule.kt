package com.saeed.marleyspoon.di

import android.content.Context
import android.content.SharedPreferences
import com.saeed.marleyspoon.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreference(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(BuildConfig.PREF_NAME, Context.MODE_PRIVATE)
    }

}