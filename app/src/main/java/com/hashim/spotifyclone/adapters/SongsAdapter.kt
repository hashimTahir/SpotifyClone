/*
 * Copyright (c) 2020/  12/ 21.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hashim.spotifyclone.R
import com.hashim.spotifyclone.data.entities.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongsAdapter @Inject constructor(
    private val hGlide: RequestManager
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    private val hDiffCall = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val hListDiffer = AsyncListDiffer(this, hDiffCall)

    var hSongsList: List<Song>
        get() = hListDiffer.currentList
        set(value) = hListDiffer.submitList(value)

    private var hOnItemClickListener: ((Song) -> Unit)? = null

    fun hSetOnItemClickListener(listner: (Song) -> Unit) {
        hOnItemClickListener = listner
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }

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

    override fun getItemCount(): Int {
        return hSongsList.size
    }

}