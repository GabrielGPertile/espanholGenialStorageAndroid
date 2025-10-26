package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.UserActivityViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.UserPerfileEditableViewHolder


class UserActivity: BaseDrawerActivity()
{
    private lateinit var userActivityViewHolder: UserActivityViewHolder
    private lateinit var userPerfileEditableViewHolder: UserPerfileEditableViewHolder

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

        loadProfilePhotoInDrawer()

        //configuração do botão
        userActivityViewHolder.btnEditar.setOnClickListener {
            navigateToUserPerfileEditableActivity()
        }
    }

    private fun navigateToUserPerfileEditableActivity()
    {
        val intent = Intent(this, UserPerfileEditableActivity::class.java)
        startActivity(intent)
    }


}