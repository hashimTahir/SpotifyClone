/*
 * Copyright (c) 2020/  12/ 25.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.support.v4.media.MediaMetadataCompat
import com.hashim.spotifyclone.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}