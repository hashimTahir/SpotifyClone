/*
 * Copyright (c) 2020/  12/ 19.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.other

open class Event<out T>(
    private val data: T
) {
    var hHasBeenHandled = false
        private set


    fun hGetContentifNotHandled(): T? {
        return if (hHasBeenHandled) {
            null
        } else {
            hHasBeenHandled = true
            data
        }
    }

    fun hPeekContent(): T {
        return data
    }

}