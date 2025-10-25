package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.UserPerfileEditableViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class UserPerfileEditableActivity: BaseDrawerActivity()
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

        //Inicializa os objetos
        userPerfileEditableViewHolder = UserPerfileEditableViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        //configura o menu lateral
        setupDrawer(
            userPerfileEditableViewHolder.drawerLayout,
            userPerfileEditableViewHolder.navView,
            userPerfileEditableViewHolder.toolbar
        )

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
                    userPerfileEditableViewHolder.ivPerfilUsuario.setImageURI(uri)
                    savedEditablePhoto(uri)
                }
            }
        }

        //configuração da edição da imagem
        userPerfileEditableViewHolder.ivPerfilUsuario.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

            pickImageLauncher.launch(intent)
        }
    }

    private fun savedEditablePhoto(uri: Uri)
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference

        val perfilRef = storageRef.child("arquivos/$userId/perfil/fotodeperfil.jpg")

        perfilRef.putFile(uri)
            .addOnSuccessListener  {
                Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao enviar foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}