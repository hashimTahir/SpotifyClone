/*
 * Copyright (c) 2020/  12/ 20.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.other.Resource
import com.hashim.spotifyclone.player.MusicServiceConnection


class MainViewModel @ViewModelInject constructor(
    private val musicServiceConnection: MusicServiceConnection
) {
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
}