/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.adapters.SwipeSongsAdapter
import com.hashim.spotifyclone.data.entities.Song
import com.hashim.spotifyclone.other.Status
import com.hashim.spotifyclone.player.isPlaying
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

    private var hPlaybackStateCompat: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hInitNavHostFragment()
        hSubscribeToObsersers()
        hSetupListeners()

        vpSong.adapter = hSwipeSongsAdapter
    }

    private fun hSetupListeners() {
        ivPlayPause.setOnClickListener {
            hCurrentPlayingSong?.let {
                hMainViewModel.hPlayOrToggerSong(it, true)
            }
        }

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (hPlaybackStateCompat?.isPlaying == true) {
                    hMainViewModel.hPlayOrToggerSong(
                        hSwipeSongsAdapter.hSongsList.get(position)
                    )
                } else {
                    hCurrentPlayingSong = hSwipeSongsAdapter.hSongsList.get(position)
                }
            }
        })

        hSwipeSongsAdapter.hSetOnItemClickListener {
            hNavController.navigate(
                R.id.hToSongFragment
            )
        }
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
                                    hLoadImageWithGlide(
                                        hCurrentPlayingSong?.imageUrl ?: songsList.get(0).imageUrl
                                    )
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

                        hLoadImageWithGlide(hCurrentPlayingSong?.imageUrl)
                        hSwitchPagerToCurrentSong(hCurrentPlayingSong ?: return@observe)
                    }
                )

                hMainViewModel.hPlayBackStateLD.observe(this, {
                    hPlaybackStateCompat = it
                    ivPlayPause.setImageResource(
                        if (hPlaybackStateCompat?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
                    )

                })
                hMainViewModel.hIsConnectedLD.observe(this, {
                    it?.hGetContentifNotHandled()?.let { result ->
                        when (result.status) {
                            Status.H_ERROR -> Snackbar.make(
                                rootLayout,
                                result.message ?: "An Error occured",
                                Snackbar.LENGTH_LONG
                            ).show()
                            else -> Unit
                        }
                    }
                })
                hMainViewModel.hIsNetworkErrorLD.observe(this, {
                    it?.hGetContentifNotHandled()?.let { result ->
                        when (result.status) {
                            Status.H_ERROR -> Snackbar.make(
                                rootLayout,
                                result.message ?: "An Error occured",
                                Snackbar.LENGTH_LONG
                            ).show()
                            else -> Unit
                        }
                    }
                })
            }
        )
    }

    private fun hInitNavHostFragment() {
        hNavHostFragment = supportFragmentManager
            .findFragmentById(R.id.hNavHostFragment) as NavHostFragment
        hNavController = hNavHostFragment.navController
        hNavController.setGraph(R.navigation.nav_graph)
        hNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.hToSongFragment -> hHideBottomBar()
                R.id.hHomeFragment -> hShoweBottomBar()
                else -> hShoweBottomBar()
            }
        }
    }

    private fun hLoadImageWithGlide(url: String?) {
        hGlide.load(url)
            .into(ivCurSongImage)

    }

    private fun hHideBottomBar() {
        ivCurSongImage.isVisible = false
        vpSong.isVisible = false
        ivPlayPause.isVisible = false
    }

    private fun hShoweBottomBar() {
        ivCurSongImage.isVisible = true
        vpSong.isVisible = true
        ivPlayPause.isVisible = true
    }
}