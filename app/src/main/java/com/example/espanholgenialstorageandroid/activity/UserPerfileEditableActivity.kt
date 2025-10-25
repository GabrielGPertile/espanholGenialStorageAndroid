package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.UserPerfileEditableViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class UserPerfileEditableActivity: AppCompatActivity()
{
    private lateinit var userPerfileEditableViewHolder: UserPerfileEditableViewHolder
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_perfile_editable_activity)
    }
}