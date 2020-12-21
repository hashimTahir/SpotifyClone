/*
 * Copyright (c) 2020/  10/ 5.  Created by Hashim Tahir
 */

package com.hashim.spotifyclone.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hashim.spotifyclone.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var hNavHostFragment: NavHostFragment
    private lateinit var hNavController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hInitNavHostFragment()

    }

    private fun hInitNavHostFragment() {
        hNavHostFragment = supportFragmentManager
            .findFragmentById(R.id.hNavHostFragment) as NavHostFragment

        hNavController = hNavHostFragment.navController
        hNavController.setGraph(R.navigation.nav_graph)

        hNavController.addOnDestinationChangedListener { _, destination, _ ->

            /* when (destination.id) {
                 R.id.hSettingsFragment, R.id.hRunFragment, R.id.hStatsFragment ->
                     bottomNavigationView.visibility = View.VISIBLE
                 else ->
                     bottomNavigationView.visibility = View.GONE
             }*/
        }
    }
}