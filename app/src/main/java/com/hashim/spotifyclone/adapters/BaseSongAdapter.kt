/*
 * Copyright (c) 2020/  12/ 25.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hashim.spotifyclone.data.entities.Song

abstract class BaseSongAdapter(
    @LayoutRes private val hLayoutRes: Int
) : RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>() {

    protected val hDiffCall = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val hListDiffer: AsyncListDiffer<Song>

    var hSongsList: List<Song>
        get() = hListDiffer.currentList
        set(value) = hListDiffer.submitList(value)

    protected var hOnItemClickListener: ((Song) -> Unit)? = null

    fun hSetOnItemClickListener(listner: (Song) -> Unit) {
        hOnItemClickListener = listner
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                hLayoutRes,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return hSongsList.size
    }
}
