package com.example.espanholgenialstorageandroid.strategy

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.espanholgenialstorageandroid.R
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageProfileImageStrategy(private val storage: FirebaseStorage) : ProfileImageStrategy
{
    override fun loadProfileImage(context: Context, imageView: ImageView, userId: String) {
        val storageRef = storage.reference
        val perfilRef = storageRef.child("arquivos/$userId/perfil/fotodeperfil.jpg")

        perfilRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.perfil_usuario)
                .into(imageView)
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.perfil_usuario)
        }
    } // <- mantém só essa
}