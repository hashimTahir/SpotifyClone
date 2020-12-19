/*
 * Copyright (c) 2020/  12/ 19.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.other.Constants

class MusicNotificationManager(
    private val context: Context,
    sessionTokken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {
    private val hNotificationManager: PlayerNotificationManager

    init {
        val hMediaControllerCompat = MediaControllerCompat(
            context,
            sessionTokken
        )

        hNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            Constants.H_NOTIFICATION_CHANNEL_ID,
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            Constants.H_NOTIFICATION_ID,
            DescriptionAdapter(hMediaControllerCompat),
            notificationListener
        ).apply {
            setSmallIcon(R.drawable.ic_music)
            setMediaSessionToken(sessionTokken)
        }
    }


    fun hShowNotification(player: Player) {
        hNotificationManager.setPlayer(player)

    }

    private inner class DescriptionAdapter(
        private val mediaControllerCompat: MediaControllerCompat
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaControllerCompat.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaControllerCompat.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaControllerCompat.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context)
                .asBitmap()
                .load(mediaControllerCompat.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
            return null
        }
    }


}