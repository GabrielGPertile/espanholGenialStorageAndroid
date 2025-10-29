package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.model.AudioDataClass
import com.example.espanholgenialstorageandroid.viewHolder.CreateAudioStorageViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateAudioStorageActivity : BaseDrawerActivity()
{
    private lateinit var createAudioStorageViewHolder: CreateAudioStorageViewHolder
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var audioDataClass: AudioDataClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_audio_storage)

        createAudioStorageViewHolder = CreateAudioStorageViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupDrawer(
            createAudioStorageViewHolder.drawerLayout,
            createAudioStorageViewHolder.navView,
            createAudioStorageViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()
    }
}