/*
 * Copyright (c) 2020/  12/ 18.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val SERVICE_TAG = "Music Service"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {


    @Inject
    lateinit var hDataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var hExoPlayer: SimpleExoPlayer

    /*Coroutine Job*/
    private val hJobService = Job()

    /*Coroutine Scope*/
    private val hServiceScoped = CoroutineScope(
        Dispatchers.Main + hJobService
    )

    private lateinit var hMediaSession: MediaSessionCompat
    private lateinit var hMediaSessionConnector: MediaSessionConnector

    private val hIsForeGroundService = false

    override fun onCreate() {
        super.onCreate()

        val hActivityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                0
            )
        }

        hMediaSession = MediaSessionCompat(this, SERVICE_TAG)
            .apply {
                setSessionActivity(hActivityIntent)
                isActive = true
            }

        sessionToken = hMediaSession.sessionToken

        hMediaSessionConnector = MediaSessionConnector(hMediaSession)
        hMediaSessionConnector.setPlayer(hExoPlayer)
    }


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        hServiceScoped.cancel()
    }
}