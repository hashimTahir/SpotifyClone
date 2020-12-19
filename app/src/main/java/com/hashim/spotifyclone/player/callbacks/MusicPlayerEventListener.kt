/*
 * Copyright (c) 2020/  12/ 19.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player.callbacks

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.hashim.spotifyclone.player.MusicService

class MusicPlayerEventListener(
    private val hMusicService: MusicService
) : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)

        if (playbackState == Player.STATE_READY && !playWhenReady) {
            hMusicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(hMusicService, error.toString(), Toast.LENGTH_LONG).show()

    }
}