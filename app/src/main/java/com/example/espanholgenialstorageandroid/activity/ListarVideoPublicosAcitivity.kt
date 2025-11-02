package com.example.espanholgenialstorageandroid.activity

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.adapter.PublicVideoAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarVideoPublicosAcitivity : BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PublicVideoAdapter
    private val listaVideos = mutableListOf<String>()
    private lateinit var selecionarVideoLauncher: ActivityResultLauncher<String>
    private var videoSelecionadoUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

}