package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.DashboardActivityViewHolder

class DashboardActivity: BaseDrawerActivity()
{
    private lateinit var dashboardActivityViewHolder: DashboardActivityViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        dashboardActivityViewHolder = DashboardActivityViewHolder(this)
        setupDrawer(
            dashboardActivityViewHolder.drawerLayout,
            dashboardActivityViewHolder.navView,
            dashboardActivityViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()

        //configurações dos botões
        dashboardActivityViewHolder.ivInserirImagem.setOnClickListener {
            navigateToCreateImage()
        }

        dashboardActivityViewHolder.ivInserirAudio.setOnClickListener {
            navigateToCreateAudio()
        }
    }

    private fun navigateToCreateImage()
    {
        val intent = Intent(this, CreatePhotoStorageActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCreateAudio()
    {
        val intent = Intent(this, CreateAudioStorageActivity::class.java)
        startActivity(intent)
    }
}