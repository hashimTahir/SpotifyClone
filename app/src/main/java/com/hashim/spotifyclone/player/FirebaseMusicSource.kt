/*
 * Copyright (c) 2020/  12/ 18.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.support.v4.media.MediaMetadataCompat
import com.hashim.spotifyclone.data.remote.MusicDataBase
import com.hashim.spotifyclone.player.State.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val hMusicDataBase: MusicDataBase
) {
    private val hOnReadyListener = mutableListOf<(Boolean) -> Unit>()
    var hSongsList = emptyList<MediaMetadataCompat>()


    private var hState = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(hOnReadyListener) {
                    field = value
                    hOnReadyListener.forEach { listener ->
                        listener(hState == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }


    suspend fun hFetchMedia() {
        withContext(Dispatchers.IO) {
            hState = STATE_INITIALIZING
            var hGetSongs = hMusicDataBase.hGetSongs()
            hSongsList = hGetSongs.map { song ->
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.subTitle)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.mediaId)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.imageUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.songUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.imageUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.subTitle)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.subTitle)
                    .build()

            }
            hState = STATE_INITIALIZED
        }
    }


    fun hWhenReady(action: (Boolean) -> Unit): Boolean {
        if (hState == STATE_CREATED || hState == STATE_INITIALIZING) {
            hOnReadyListener += action
            return false
        } else {
            action(hState == STATE_INITIALIZED)
            return true

        }

    }
}


enum class State {
    STATE_CREATED,
    STATE_INITIALIZED,
    STATE_INITIALIZING,
    STATE_ERROR
}