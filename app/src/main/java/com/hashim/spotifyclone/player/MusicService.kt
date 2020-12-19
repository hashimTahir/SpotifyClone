/*
 * Copyright (c) 2020/  12/ 18.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.player.callbacks.MusicPlaybacePreparer
import com.hashim.spotifyclone.player.callbacks.MusicPlayerEventListener
import com.hashim.spotifyclone.player.callbacks.MusicPlayerNotificaitonListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
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
    private lateinit var hMusicPlayerEventListener: MusicPlayerEventListener

    private var hCurrentlyPlayingSong: MediaMetadataCompat? = null

    var hIsForeGroundService = false
    private var hIsPlayerInitilized = false

    companion object {
        var hCurrentSongDuration = 0L
            /*Value can only be changed from within the service*/
            private set
    }

    override fun onCreate() {
        super.onCreate()

        hServiceScoped.launch {
            hFirebaseMusicSource.hFetchMedia()
        }
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
            hCurrentSongDuration = hExoPlayer.contentDuration

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

        hMediaSessionConnector.setQueueNavigator(MusiceQueueNavigator())

        hMediaSessionConnector.setPlayer(hExoPlayer)

        hMusicPlayerEventListener = MusicPlayerEventListener(this)
        hExoPlayer.addListener(hMusicPlayerEventListener)

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
        return BrowserRoot(Constants.H_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            Constants.H_MEDIA_ROOT_ID -> {
                val hResultsSent = hFirebaseMusicSource.hWhenReady { isInitilized ->
                    if (isInitilized) {
                        result.sendResult(hFirebaseMusicSource.hAsMediaItems())
                        if (!hIsPlayerInitilized && hFirebaseMusicSource.hSongsList.isNotEmpty()) {
                            hPreparePlayer(
                                hFirebaseMusicSource.hSongsList,
                                hFirebaseMusicSource.hSongsList[0],
                                false
                            )
                            hIsPlayerInitilized = true
                        } else {
                            result.sendResult(null)
                        }
                    }
                }
                if (!hResultsSent) {
                    result.detach()
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        hExoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        hServiceScoped.cancel()
        hExoPlayer.removeListener(hMusicPlayerEventListener)
        hExoPlayer.release()

    }

    private inner class MusiceQueueNavigator : TimelineQueueNavigator(hMediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return hFirebaseMusicSource.hSongsList[windowIndex].description
        }
    }
}