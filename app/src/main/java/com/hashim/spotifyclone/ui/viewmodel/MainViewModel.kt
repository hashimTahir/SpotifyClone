/*
 * Copyright (c) 2020/  12/ 20.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.other.Resource
import com.hashim.spotifyclone.player.MusicServiceConnection
import com.hashim.spotifyclone.player.isPlayEnabled
import com.hashim.spotifyclone.player.isPlaying
import com.hashim.spotifyclone.player.isPrepared


class MainViewModel @ViewModelInject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val hMediaItemsMLD = MutableLiveData<Resource<List<Song>>>()
    val hMediaItemsLD: LiveData<Resource<List<Song>>> = hMediaItemsMLD


    val hIsConnectedLD = musicServiceConnection.hIsConnectedLD
    val hIsNetworkErrorLD = musicServiceConnection.hIsNetworkErrorLD
    val hCurrentlyPlayingSongLD = musicServiceConnection.hCurrentPlayingSongLD
    val hPlayBackStateLD = musicServiceConnection.hPlaybackStateLD

    init {
        hMediaItemsMLD.postValue(
            Resource.hLoading(null)
        )
        musicServiceConnection.hSubscribe(
            Constants.H_MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    val hList = children.map {
                        Song(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri.toString(),
                            it.description.iconUri.toString()
                        )
                    }
                    hMediaItemsMLD.postValue(
                        Resource.hSuccess(
                            hList
                        )
                    )
                }
            }
        )
    }

    fun hSkipToNextSong() {
        musicServiceConnection.hTransportControls.skipToNext()
    }

    fun hSkipToPreviousSong() {
        musicServiceConnection.hTransportControls.skipToPrevious()
    }

    fun hSeekTo(pos: Long) {
        musicServiceConnection.hTransportControls.seekTo(pos)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.hUnSubscribe(
            Constants.H_MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
            }
        )
    }

    fun hPlayOrToggerSong(mediaItem: Song, toggle: Boolean = false) {
        val hIsPrepared = hPlayBackStateLD.value?.isPrepared ?: false

        if (hIsPrepared && mediaItem.mediaId == hCurrentlyPlayingSongLD?.value?.getString(
                METADATA_KEY_MEDIA_ID
            )
        ) {
            hPlayBackStateLD.value?.let { playbackStateCompat ->
                when {
                    playbackStateCompat.isPlaying -> if (toggle)
                        musicServiceConnection.hTransportControls.pause()
                    playbackStateCompat.isPlayEnabled -> musicServiceConnection.hTransportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.hTransportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }
}