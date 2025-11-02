package com.example.espanholgenialstorageandroid.activity

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.adapter.PrivateAudioAdapter
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarAudioPublicosAcitivity: BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrivateAudioAdapter
    private val listaAudios = mutableListOf<String>()
    private lateinit var selecionarAudioLauncher: ActivityResultLauncher<String>
    private var audioSelecionadaUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()
}