package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.strategy.FirebaseStorageProfileImageStrategy
import com.example.espanholgenialstorageandroid.viewHolder.UserActivityViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.UserPerfileEditableViewHolder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth


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

        loadProfilePhotoInDrawer()
        loadProfilePhotoWithStrategy()

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

    private fun loadProfilePhotoWithStrategy() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val strategy = FirebaseStorageProfileImageStrategy(FirebaseStorage.getInstance())

        strategy.loadProfileImage(
            context = this,
            imageView = userActivityViewHolder.ivPerfilUsuario,
            userId = userId
        )
    }

}