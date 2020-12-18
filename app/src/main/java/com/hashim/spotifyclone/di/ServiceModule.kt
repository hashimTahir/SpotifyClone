/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.di

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.data.remote.MusicDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @Provides
    @ServiceScoped
    fun hProvidesAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    @Provides
    @ServiceScoped
    fun hProvidesExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .build().apply {
                setAudioAttributes(audioAttributes, true)
                setHandleAudioBecomingNoisy(true)
            }
    }

    @Provides
    @ServiceScoped
    fun hProvidesDataSourceFactory(
        @ApplicationContext context: Context,

        ): DefaultDataSourceFactory {
        return DefaultDataSourceFactory(
            context,
            Util.getUserAgent(
                context,
                context.resources.getString(R.string.app_name)
            )
        )
    }


    @Provides
    @ServiceScoped
    fun hProvidesMusicDatabase(): MusicDataBase {
        return MusicDataBase()
    }
}