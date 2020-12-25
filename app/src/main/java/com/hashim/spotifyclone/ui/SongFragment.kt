/*
 * Copyright (c) 2020/  12/ 25.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Status
import com.hashim.spotifyclone.player.toSong
import com.hashim.spotifyclone.ui.viewmodel.MainViewModel
import com.hashim.spotifyclone.ui.viewmodel.SongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var hGlide: RequestManager

    private lateinit var hMainViewModel: MainViewModel
    private val hSongsViewModel: SongsViewModel by viewModels()

    private var hCurrentlyPlaingSong: Song? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hMainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        hSubscribeObservers()
    }

    private fun hSubscribeObservers() {
        hMainViewModel.hMediaItemsLD.observe(
            viewLifecycleOwner, {
                it?.let {
                    when (it.status) {
                        Status.H_SUCCESS -> {
                            it.data?.let { songList ->
                                if (hCurrentlyPlaingSong == null && songList.isEmpty()) {
                                    hCurrentlyPlaingSong = songList.get(0)
                                    hUpdateTitleNSongImage(songList.get(0))
                                }
                            }
                        }
                        else -> Unit
                    }
                }
            }
        )
        hMainViewModel.hCurrentlyPlayingSongLD.observe(
            viewLifecycleOwner, {
                if (it == null) return@observe
                hCurrentlyPlaingSong = it.toSong()
                hUpdateTitleNSongImage(hCurrentlyPlaingSong!!)
            })
    }

    private fun hUpdateTitleNSongImage(song: Song) {
        val hTitle = "${song.title} - ${song.subTitle}"
        tvSongName.text = hTitle
        hGlide.load(song.imageUrl).into(ivSongImage)
    }
}