/*
 * Copyright (c) 2020/  12/ 25.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Status
import com.hashim.spotifyclone.player.isPlaying
import com.hashim.spotifyclone.player.toSong
import com.hashim.spotifyclone.ui.viewmodel.MainViewModel
import com.hashim.spotifyclone.ui.viewmodel.SongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var hGlide: RequestManager

    private lateinit var hMainViewModel: MainViewModel
    private val hSongsViewModel: SongsViewModel by viewModels()

    private var hCurrentlyPlaingSong: Song? = null


    private var hPlaybackStateCompat: PlaybackStateCompat? = null

    private var hShouldUpdateSeekBar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hMainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        hSubscribeObservers()
        hSetupListener()

    }

    private fun hSetupListener() {

        ivPlayPauseDetail.setOnClickListener {
            hCurrentlyPlaingSong?.let {
                hMainViewModel.hPlayOrToggerSong(it, true)
            }
        }

        ivSkipPrevious.setOnClickListener {
            hMainViewModel.hSkipToPreviousSong()
        }
        ivSkip.setOnClickListener {
            hMainViewModel.hSkipToNextSong()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    hSetCurrentPlayerTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                hShouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    hMainViewModel.hSeekTo(it.progress.toLong())
                    hShouldUpdateSeekBar = true
                }
            }
        })
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

        hMainViewModel.hPlayBackStateLD.observe(
            viewLifecycleOwner, {
                hPlaybackStateCompat = it
                ivPlayPauseDetail.setImageResource(
                    if (hPlaybackStateCompat?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                )
                seekBar.progress = it?.position?.toInt() ?: 0
            }
        )
        hSongsViewModel.hCurrentPlayerPosLD.observe(
            viewLifecycleOwner, {
                if (hShouldUpdateSeekBar) {
                    seekBar.progress = it.toInt()
                    hSetCurrentPlayerTime(it)

                }
            }
        )
        hSongsViewModel.hCurrentSongDurationLD.observe(
            viewLifecycleOwner, {
                seekBar.max = it.toInt()

                val hSimpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                tvSongDuration.text = hSimpleDateFormat.format(it)
            }
        )
    }

    private fun hSetCurrentPlayerTime(timeMs: Long) {
        val hSimpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tvCurTime.text = hSimpleDateFormat.format(timeMs)

    }

    private fun hUpdateTitleNSongImage(song: Song) {
        val hTitle = "${song.title} - ${song.subTitle}"
        tvSongName.text = hTitle
        hGlide.load(song.imageUrl).into(ivSongImage)
    }
}