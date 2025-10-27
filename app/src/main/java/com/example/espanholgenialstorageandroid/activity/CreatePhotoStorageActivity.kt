package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.model.UserClass
import com.example.espanholgenialstorageandroid.viewHolder.CreatePhotoStorageViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class CreatePhotoStorageActivity : BaseDrawerActivity()
{
    private lateinit var createPhotoStorageViewHolder: CreatePhotoStorageViewHolder
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var user: UserClass

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_photo_storage)

        createPhotoStorageViewHolder = CreatePhotoStorageViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        setupDrawer(
            createPhotoStorageViewHolder.drawerLayout,
            createPhotoStorageViewHolder.navView,
            createPhotoStorageViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if(result.resultCode == RESULT_OK)
            {
                val data: Intent? = result.data
                val uri = data?.data

                if(uri != null)
                {
                    selectedImageUri = uri

                    val bitmap = getCorrectlyOrientedBitmap(uri)

                    if (bitmap != null) {
                        createPhotoStorageViewHolder.ivPhoto.setImageBitmap(bitmap)
                    }
                }
            }
        }

        //configuração dos botões
        createPhotoStorageViewHolder.btnCanelar.setOnClickListener {
            cancelInsertPhoto()
        }
    }

    private fun cancelInsertPhoto()
    {
       /* val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/
        finish()
    }
}