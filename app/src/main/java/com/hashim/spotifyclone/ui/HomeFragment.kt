/*
 * Copyright (c) 2020/  12/ 21.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.adapters.SongsAdapter
import com.hashim.spotifyclone.other.Status
import com.hashim.spotifyclone.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var hMainViewModel: MainViewModel

    @Inject
    lateinit var hSongsAdapter: SongsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hMainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        hInitRecyclerView()
        hSubscribeObservers()

        hSongsAdapter.hSetOnItemClickListener {
            hMainViewModel.hPlayOrToggerSong(it)
        }
    }

    private fun hInitRecyclerView() {
        rvAllSongs.apply {
            adapter = hSongsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun hSubscribeObservers() {
        hMainViewModel.hMediaItemsLD.observe(
            viewLifecycleOwner, { result ->
                when (result.status) {
                    Status.H_SUCCESS -> {
                        allSongsProgressBar.isVisible = false
                        result.data?.let { songs ->
                            hSongsAdapter.hSongsList = songs
                        }
                    }
                    Status.H_ERROR -> Unit
                    Status.H_LOADING -> allSongsProgressBar.isVisible = true
                }

            }
        )
    }
}