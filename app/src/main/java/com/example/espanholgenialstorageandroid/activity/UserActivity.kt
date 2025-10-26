package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.strategy.FirebaseStorageProfileImageStrategy
import com.example.espanholgenialstorageandroid.viewHolder.UserActivityViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class UserActivity: BaseDrawerActivity()
{
    private lateinit var userActivityViewHolder: UserActivityViewHolder
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_perfile_activity)

        userActivityViewHolder = UserActivityViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setupDrawer(
            userActivityViewHolder.drawerLayout,
            userActivityViewHolder.navView,
            userActivityViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()
        loadProfilePhotoWithStrategy()
        loadUserdata()

        //configuração do botão
        userActivityViewHolder.btnEditar.setOnClickListener {
            navigateToUserPerfileEditableActivity()
        }
    }

    private fun navigateToUserPerfileEditableActivity()
    {
        val intent = Intent(this, UserPerfileEditableActivity::class.java)
        startActivity(intent)
    }

    private fun loadProfilePhotoWithStrategy() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val strategy = FirebaseStorageProfileImageStrategy(FirebaseStorage.getInstance())

        strategy.loadProfileImage(
            context = this,
            imageView = userActivityViewHolder.ivPerfilUsuario,
            userId = userId
        )
    }

    private fun loadUserdata()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            if(snapshot.exists())
            {
                val data = snapshot.value as? Map<*, *>
                val nome = data?.get("nomeCompleto") as? String ?: ""
                val idade = (data?.get("idade") as? Long)?.toInt() ?: 0
                val email = data?.get("email") as? String ?: ""

                val displayNome = if (nome.isEmpty()) "Não há registro de nome" else nome
                val displayIdade = if (idade == 0) "Não há registro de idade" else idade.toString()
                val displayEmail = if (email.isEmpty()) "Não há registro de email" else email

                userActivityViewHolder.tvNomeCompletoDado.text = displayNome
                userActivityViewHolder.tvIdadeDado.text = displayIdade
                userActivityViewHolder.tvEmailDado.text = displayEmail

            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}