/*
 * Copyright (c) 2020/  12/ 18.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.player

import com.hashim.spotifyclone.player.State.*

class FirebaseMusicSource {
    private val hOnReadyListener = mutableListOf<(Boolean) -> Unit>()
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