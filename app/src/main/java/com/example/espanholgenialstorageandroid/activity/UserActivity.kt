package com.example.espanholgenialstorageandroid.activity

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

        //configuração do botão
        userActivityViewHolder.btnEditar.setOnClickListener {
            // Troca o layout para o de edição
            setContentView(R.layout.user_perfile_editable_activity)

            userPerfileEditableViewHolder = UserPerfileEditableViewHolder(this)

            setupDrawer(
                userPerfileEditableViewHolder.drawerLayout,
                userPerfileEditableViewHolder.navView,
                userPerfileEditableViewHolder.toolbar
            )
        }
    }

}