/*
 * Copyright (c) 2020/  12/ 18.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.hashim.spotifyclone.player.callbacks.MusicPlaybacePreparer
import com.hashim.spotifyclone.player.callbacks.MusicPlayerEventListener
import com.hashim.spotifyclone.player.callbacks.MusicPlayerNotificaitonListener
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

    @Inject
    lateinit var hFirebaseMusicSource: FirebaseMusicSource

    /*Coroutine Job*/
    private val hJobService = Job()

    /*Coroutine Scope*/
    private val hServiceScoped = CoroutineScope(
        Dispatchers.Main + hJobService
    )

    private lateinit var hMediaSession: MediaSessionCompat
    private lateinit var hMediaSessionConnector: MediaSessionConnector
    private lateinit var hMusicNotificationManager: MusicNotificationManager

    private var hCurrentlyPlayingSong: MediaMetadataCompat? = null

    var hIsForeGroundService = false


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

        hMusicNotificationManager = MusicNotificationManager(
            this,
            hMediaSession.sessionToken,
            MusicPlayerNotificaitonListener(
                this
            )
        ) {


        }

        val hMusicPlaybacePreparer = MusicPlaybacePreparer(hFirebaseMusicSource) {
            hCurrentlyPlayingSong = it
            hPreparePlayer(
                hFirebaseMusicSource.hSongsList,
                it,
                true
            )
        }

        hMediaSessionConnector = MediaSessionConnector(hMediaSession)

        hMediaSessionConnector.setPlaybackPreparer(hMusicPlaybacePreparer)

        hMediaSessionConnector.setPlayer(hExoPlayer)

        hExoPlayer.addListener(MusicPlayerEventListener(this))

        hMusicNotificationManager.hShowNotification(hExoPlayer)
    }


    private fun hPreparePlayer(
        songsList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val hCurrentItemIndex =
            if (hCurrentlyPlayingSong == null) 0 else songsList.indexOf(itemToPlay)
        hExoPlayer.prepare(hFirebaseMusicSource.hConvertToPlayList(hDataSourceFactory))
        hExoPlayer.seekTo(hCurrentItemIndex, 0L)
        hExoPlayer.playWhenReady = playNow

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