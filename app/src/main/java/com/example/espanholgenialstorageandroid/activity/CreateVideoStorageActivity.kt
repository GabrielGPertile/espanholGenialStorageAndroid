package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.example.espanholgenialstorageandroid.model.ImageDataClass
import com.example.espanholgenialstorageandroid.viewHolder.CreateVideoStorageViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateVideoStorageActivity : BaseDrawerActivity()
{
    private lateinit var createVideoStorageViewHolder: CreateVideoStorageViewHolder
    private lateinit var pickVideoLauncher: ActivityResultLauncher<Intent>
    private var selectedVideoUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var videoDataClass: ImageDataClass

}