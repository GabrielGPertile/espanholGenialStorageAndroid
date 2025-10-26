package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.CreatePhotoStorageViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.DashboardActivityViewHolder

class CreatePhotoStorageActivity : BaseDrawerActivity()
{
    private lateinit var createPhotoStorageViewHolder: CreatePhotoStorageViewHolder

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_photo_storage)

        createPhotoStorageViewHolder = CreatePhotoStorageViewHolder(this)

        setupDrawer(
            createPhotoStorageViewHolder.drawerLayout,
            createPhotoStorageViewHolder.navView,
            createPhotoStorageViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()
    }
}