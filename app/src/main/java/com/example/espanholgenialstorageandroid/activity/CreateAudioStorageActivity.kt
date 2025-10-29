package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.model.AudioDataClass
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.example.espanholgenialstorageandroid.viewHolder.CreateAudioStorageViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateAudioStorageActivity: BaseDrawerActivity()
{
    private lateinit var createAudioStorageViewHolder: CreateAudioStorageViewHolder
    private lateinit var pickAudioLauncher: ActivityResultLauncher<Intent>
    private var mediaPlayer: MediaPlayer? = null
    private var selectedAudioUri: Uri? = null
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


        pickAudioLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedAudioUri = data?.data

                if (selectedAudioUri != null) {
                    Toast.makeText(this, "Áudio selecionado!", Toast.LENGTH_SHORT).show()

                    // Mostra um ícone de áudio ou animação no ImageView
                    createAudioStorageViewHolder.ivAudio.setImageResource(R.drawable.ic_audio_wave)

                    // Agora o ImageView funciona como botão de play
                    createAudioStorageViewHolder.ivAudio.setOnClickListener {
                        selectedAudioUri?.let { uri ->
                            mediaPlayer?.release() // libera player antigo
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(this@CreateAudioStorageActivity, uri)
                                prepare()
                                start()
                            }
                        }
                    }
                }
            }
        }

        //configuração dos botões
        createAudioStorageViewHolder.ivAudio.setOnClickListener {
            if (selectedAudioUri == null) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "audio/*"
                pickAudioLauncher.launch(intent)
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(this@CreateAudioStorageActivity, selectedAudioUri!!)
                    prepare()
                    start()
                }
            }
        }

        createAudioStorageViewHolder.btnSalvar.setOnClickListener {
            saveAudioStorage()
        }

        createAudioStorageViewHolder.btnCanelar.setOnClickListener {
            cancelInsertAuio()
        }
    }

    private fun saveAudioStorage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val rawName = createAudioStorageViewHolder.etAudioName.text.toString().trim()

        if (selectedAudioUri == null || rawName.isEmpty()) {
            Toast.makeText(this, "Selecione um áudio e digite o nome", Toast.LENGTH_SHORT).show()
            return
        }

        val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()

        val sanitizedFileName = try {
            val sanitized = sanitizer.sanitizeFileName(rawName)

            sanitized?.lowercase() ?: return
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        val audioRef = storage.reference.child("arquivos/$userId/audiosPrivados/${sanitizedFileName}.mp3")

        audioRef.putFile(selectedAudioUri!!)
            .addOnSuccessListener {
                Toast.makeText(this, "Áudio salvo com sucesso!", Toast.LENGTH_LONG).show()
                createAudioStorageViewHolder.etAudioName.text?.clear()
                selectedAudioUri = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao enviar áudio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelInsertAuio()
    {
        finish()
    }
}