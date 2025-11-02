package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PrivatePhotoAdapter
import com.example.espanholgenialstorageandroid.adapter.PublicPhotoAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarFotoPublicasAcitivity : BaseDrawerActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PublicPhotoAdapter
    private val listaImagens = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_imagens_publicas)

        // 🔹 Inicializa as views do Drawer e Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val drawerLayout =
            findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navView =
            findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupDrawer(
            drawerLayout,
            navView,
            toolbar
        )

        loadProfilePhotoInDrawer()

        recyclerView = findViewById(R.id.recyclerViewImagens)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PublicPhotoAdapter(
            listaImagens,
            onVisualizar = { nome -> visualizarImagem(nome) },
            onExcluir = { nome -> excluirImagem(nome) },
            onTornarPublico = { nome -> tornarImagemPublica(nome) }
        )
        recyclerView.adapter = adapter

        carregarNomesImagens()
    }

    private fun carregarNomesImagens() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef =
            storage.reference.child("arquivos/$userId/imagensPrivadas/") // pasta no Storage

        storageRef.listAll()
            .addOnSuccessListener { lista ->
                for (item in lista.items) {
                    listaImagens.add(item.name) // pega só o nome do arquivo
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun visualizarImagem(nome: String) {
        Toast.makeText(this, "Visualizar: $nome", Toast.LENGTH_SHORT).show()
    }

    private fun excluirImagem(nome: String) {
        Toast.makeText(this, "Excluir: $nome", Toast.LENGTH_SHORT).show()
    }

    private fun tornarImagemPublica(nome: String) {
        Toast.makeText(this, "Tornar público: $nome", Toast.LENGTH_SHORT).show()
    }
}