/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hashim.spotifyclone.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}