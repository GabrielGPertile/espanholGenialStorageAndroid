package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.DashboardActivityViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.UserActivityViewHolder


class UserActivity: BaseDrawerActivity()
{
    private lateinit var userActivityViewHolder: UserActivityViewHolder

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_perfile_activity)

        userActivityViewHolder = UserActivityViewHolder(this)

        setupDrawer(
            userActivityViewHolder.drawerLayout,
            userActivityViewHolder.navView,
            userActivityViewHolder.toolbar
        )

    }
}