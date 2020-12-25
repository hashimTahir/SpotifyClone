/*
 * Copyright (c) 2020/  12/ 25.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashim.spotifyclone.other.Constants
import com.hashim.spotifyclone.player.MusicService
import com.hashim.spotifyclone.player.MusicServiceConnection
import com.hashim.spotifyclone.player.currentPlaybackPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongsViewModel @ViewModelInject constructor(
    private val hMusicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val hPlaybackState = hMusicServiceConnection.hPlaybackStateLD

    private val hCurrentSongDurationMLD = MutableLiveData<Long>()
    val hCurrentSongDurationLD = hCurrentSongDurationMLD


    private val hCurrentPlayerPosMLD = MutableLiveData<Long>()
    val hCurrentPlayerPosLD = hCurrentPlayerPosMLD

    init {
        hUpdatePlayerPosition()
    }

    private fun hUpdatePlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val hPosition = hPlaybackState.value?.currentPlaybackPosition
                if (hCurrentPlayerPosLD.value != hPosition) {
                    hCurrentPlayerPosMLD.postValue(hPosition)
                    hCurrentSongDurationMLD.postValue(MusicService.hCurrentSongDuration)
                }
                delay(Constants.H_UPDATE_PLAYER_INTERVAL)
            }
        }
    }

}