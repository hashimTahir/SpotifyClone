/*
 * Copyright (c) 2020/  12/ 20.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.other.Event
import com.hashim.spotifyclone.other.Resource

class MusicServiceConnection(
    context: Context
) {
    private val hIsConnectedMLD = MutableLiveData<Event<Resource<Boolean>>>()
    val hIsConnectedLD: LiveData<Event<Resource<Boolean>>> = hIsConnectedMLD

    private val hIsNetworkErrorMLD = MutableLiveData<Event<Resource<Boolean>>>()
    val hIsNetworkErrorLD: LiveData<Event<Resource<Boolean>>> = hIsNetworkErrorMLD

    private val hPlaybackStateMLD = MutableLiveData<PlaybackStateCompat?>()
    val hPlaybackStateLD: LiveData<PlaybackStateCompat?> = hPlaybackStateMLD

    private val hCurrentPlayingSongMLD = MutableLiveData<MediaMetadataCompat?>()
    val hCurrentPlayingSongLD: LiveData<MediaMetadataCompat?> = hCurrentPlayingSongMLD

    lateinit var hMediaControllerCompat: MediaControllerCompat

    private val hMediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val hMediaBrowserCompat = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        hMediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    val hTransportControls: MediaControllerCompat.TransportControls
        get() = hMediaControllerCompat.transportControls


    fun hSubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        hMediaBrowserCompat.subscribe(parentId, callback)
    }

    fun hUnSubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        hMediaBrowserCompat.unsubscribe(parentId, callback)
    }

    private inner class MediaControllerCallbacks : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            hPlaybackStateMLD.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            hCurrentPlayingSongMLD.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                Constants.H_NETWORK_ERROR -> hIsNetworkErrorMLD.postValue(
                    Event(
                        Resource.hError(
                            "Couldnt connect to service",
                            null
                        )
                    )
                )

            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            hMediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            hMediaControllerCompat = MediaControllerCompat(
                context,
                hMediaBrowserCompat.sessionToken
            ).apply {
                registerCallback(
                    MediaControllerCallbacks()
                )
            }
            hIsConnectedMLD.postValue(
                Event(
                    Resource.hSuccess(
                        true
                    )
                )
            )
        }

        override fun onConnectionSuspended() {
            hIsConnectedMLD.postValue(
                Event(
                    Resource.hError(
                        "The connection was suspended",
                        false
                    )
                )
            )
        }

        override fun onConnectionFailed() {
            hIsConnectedMLD.postValue(
                Event(
                    Resource.hError(
                        "Couldnt connect to media browser",
                        false
                    )
                )
            )
        }

    }

}