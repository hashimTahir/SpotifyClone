/*
 * Copyright (c) 2020/  12/ 19.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.other

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> hSuccess(data: T?): Resource<T> {
            return Resource(Status.H_SUCCESS, data, null)
        }

        fun <T> hError(message: String, data: T?): Resource<T> {
            return Resource(Status.H_ERROR, data, message)
        }


        fun <T> hLoading(data: T?): Resource<T> {
            return Resource(Status.H_LOADING, data, null)
        }
    }

}

enum class Status {
    H_SUCCESS,
    H_ERROR,
    H_LOADING
}