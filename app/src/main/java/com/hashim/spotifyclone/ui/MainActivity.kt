/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.RequestManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.adapters.SwipeSongsAdapter
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Status
import com.hashim.spotifyclone.player.toSong
import com.hashim.spotifyclone.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var hNavHostFragment: NavHostFragment
    private lateinit var hNavController: NavController

    private val hMainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var hSwipeSongsAdapter: SwipeSongsAdapter

    @Inject
    lateinit var hGlide: RequestManager

    private var hCurrentPlayingSong: Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hInitNavHostFragment()
        hSubscribeToObsersers()

        vpSong.adapter = hSwipeSongsAdapter
    }

    private fun hSwitchPagerToCurrentSong(song: Song) {
        val hItemIndex = hSwipeSongsAdapter.hSongsList.indexOf(song)

        if (hItemIndex != -1) {
            vpSong.currentItem = hItemIndex
            hCurrentPlayingSong = song
        }
    }

    private fun hSubscribeToObsersers() {
        hMainViewModel.hMediaItemsLD.observe(
            this, Observer {
                it?.let { songResourse ->
                    when (songResourse.status) {
                        Status.H_SUCCESS -> {
                            songResourse.data?.let { songsList ->
                                hSwipeSongsAdapter.hSongsList = songsList
                                if (songsList.isNotEmpty()) {
                                    hGlide.load(
                                        (hCurrentPlayingSong ?: songsList.get(0).imageUrl)
                                    )
                                        .into(ivCurSongImage)
                                }
                                hSwitchPagerToCurrentSong(hCurrentPlayingSong ?: return@Observer)
                            }

                        }
                        Status.H_ERROR -> Unit
                        Status.H_LOADING -> Unit
                    }
                }
                hMainViewModel.hCurrentlyPlayingSongLD.observe(
                    this, {
                        if (it == null) return@observe

                        hCurrentPlayingSong = it.toSong()
                        hGlide.load(
                            hCurrentPlayingSong?.imageUrl
                        )
                            .into(ivCurSongImage)
                        hSwitchPagerToCurrentSong(hCurrentPlayingSong ?:return@observe)
                    }
                )


            }
        )
    }

    private fun hInitNavHostFragment() {
        hNavHostFragment = supportFragmentManager
            .findFragmentById(R.id.hNavHostFragment) as NavHostFragment
        hNavController = hNavHostFragment.navController
        hNavController.setGraph(R.navigation.nav_graph)
        hNavController.addOnDestinationChangedListener { _, destination, _ ->
        }
    }
}