package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import kotlin.Triple
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.CarrosselAdapter
import com.example.espanholgenialstorageandroid.adapter.CarrosselItem
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SobreNosActivity : BaseDrawerActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_nos)

        // 🔹 Inicializa o Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 🔹 Inicializa as views do Drawer e Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)

        setupDrawer(drawerLayout, navView, toolbar)
        loadProfilePhotoInDrawer()

        // 🔹 ViewPager do carrossel
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerSobreNos)

        // 🔹 Cria referências para as imagens no Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("arquivos/logosSobreNos")

        // 🔹 Arquivos e descrições
        val imagens: List<Pair<String, String>> = listOf(
            "logo.jpeg" to "Educação e tecnologia estão cada vez mais interligadas, tornando-se elementos centrais na vida cotidiana. Esse avanço permite que os indivíduos conciliem estudos com suas rotinas, aproveitando horários flexíveis e recursos acessíveis. Nesse contexto, surge o Projeto Español Genial, cujo objetivo é integrar tecnologia e ensino de línguas estrangeiras.",
            "iconeSocia.jpeg" to "Claudia Maria Ferro Mazzarollo — Bacharel em Turismo, especialista em Gestão de Pessoas e docente em cursos de espanhol e turismo.",
            "iconeProgramador.jpg" to "Gabriel Gasperin Pertile — Desenvolvedor do app Español Genial e estudante de Análise e Desenvolvimento de Sistemas no IFRS."
        )

        // 🔹 Lista temporária para os itens
        val listaCarrossel = mutableListOf<CarrosselItem>()

        // 🔹 Busca as imagens do Firebase Storage
        imagens.forEach { (nomeArquivo, descricao) ->
            val imagemRef = storageRef.child(nomeArquivo)
            imagemRef.downloadUrl
                .addOnSuccessListener { uri ->
                    listaCarrossel.add(CarrosselItem(uri.toString(), descricao))

                    // Quando todas forem carregadas, define o adapter
                    if (listaCarrossel.size == imagens.size) {
                        viewPager.adapter = CarrosselAdapter(listaCarrossel)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao carregar imagem $nomeArquivo: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
