/*
 * Copyright (c) 2020/  12/ 21.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.hashim.spotifyclone.R
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongsAdapter @Inject constructor(
    private val hGlide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {
    override val hListDiffer = AsyncListDiffer(this, hDiffCall)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val hSong = hSongsList.get(position)
        holder.itemView.apply {
            tvPrimary.text = hSong.title
            tvSecondary.text = hSong.subTitle
            hGlide.load(hSong.imageUrl)
                .into(ivItemImage)

            setOnClickListener {
                hOnItemClickListener?.let { click ->
                    click(hSong)
                }
            }
        }
    }

}