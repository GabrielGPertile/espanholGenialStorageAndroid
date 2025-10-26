package com.example.espanholgenialstorageandroid.activity

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
    }
}