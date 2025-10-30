package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.model.ImageDataClass
import com.example.espanholgenialstorageandroid.viewHolder.CreatePhotoStorageViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.CreateVideoStorageViewHolder
import com.google.firebase.FirebaseApp
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_video_storage)

        createVideoStorageViewHolder = CreateVideoStorageViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupDrawer(
            createVideoStorageViewHolder.drawerLayout,
            createVideoStorageViewHolder.navView,
            createVideoStorageViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()

        pickVideoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data

                if (uri != null) {
                    selectedVideoUri = uri

                    // Esconde a imagem e mostra o vídeo
                    createVideoStorageViewHolder.ivVideo.visibility = View.GONE
                    createVideoStorageViewHolder.videoView.visibility = View.VISIBLE

                    // Exibe o vídeo no VideoView
                    createVideoStorageViewHolder.videoView.setVideoURI(uri)
                    createVideoStorageViewHolder.videoView.start()

                    Toast.makeText(this, "🎬 Vídeo selecionado!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //configuração dos botões
        createVideoStorageViewHolder.ivVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "video/*"
            }

            pickVideoLauncher.launch(intent)
        }
    }
}