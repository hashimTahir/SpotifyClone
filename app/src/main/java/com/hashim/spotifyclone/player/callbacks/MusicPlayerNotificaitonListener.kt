/*
 * Copyright (c) 2020/  12/ 19.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.player.MusicService

class MusicPlayerNotificaitonListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(true)
            hIsForeGroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing && !hIsForeGroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(
                        applicationContext,
                        this::class.java
                    )
                )
                startForeground(Constants.H_NOTIFICATION_ID, notification)
                hIsForeGroundService = true
            }

        }
    }
}