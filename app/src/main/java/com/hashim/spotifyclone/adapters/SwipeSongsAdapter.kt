/*
 * Copyright (c) 2020/  12/ 21.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.hashim.spotifyclone.R
import kotlinx.android.synthetic.main.swipe_item.view.*

class SwipeSongsAdapter : BaseSongAdapter(R.layout.swipe_item) {
    override val hListDiffer = AsyncListDiffer(this, hDiffCall)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val hSong = hSongsList.get(position)
        holder.itemView.apply {
            val hText = "${hSong.title}-${hSong.subTitle}"
            tvPrimary.text = hText
            setOnClickListener {
                hOnItemClickListener?.let { click ->
                    click(hSong)
                }
            }
        }
    }

}