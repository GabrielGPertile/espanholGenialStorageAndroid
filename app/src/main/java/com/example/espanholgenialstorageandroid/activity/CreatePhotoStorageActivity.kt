package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
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

        //configuração dos botões
        createPhotoStorageViewHolder.btnCanelar.setOnClickListener {
            cancelInsertPhoto()
        }
    }

    private fun cancelInsertPhoto()
    {
       /* val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/
        finish()
    }
}