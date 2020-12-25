/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Constants
import kotlinx.coroutines.tasks.await

class MusicDataBase {
    private val hFirestore = FirebaseFirestore.getInstance()
    private val hSongCollection = hFirestore.collection(Constants.H_SONGS)

    suspend fun hGetSongs(): List<Song> {
        return try {
            hSongCollection.get().await().toObjects(Song::class.java)

        } catch (e: Exception) {
            emptyList()
        }
    }
}