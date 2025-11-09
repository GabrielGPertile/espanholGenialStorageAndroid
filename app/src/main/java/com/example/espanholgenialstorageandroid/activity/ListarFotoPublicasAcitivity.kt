package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PrivatePhotoAdapter
import com.example.espanholgenialstorageandroid.adapter.PublicPhotoAdapter
import com.example.espanholgenialstorageandroid.fragment.VisualizarImagemPrivadaDialogFragment
import com.example.espanholgenialstorageandroid.fragment.VisualizarImagemPublicDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarFotoPublicasAcitivity : BaseDrawerActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PublicPhotoAdapter
    private val listaImagens = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var btnCasoDeUso: FloatingActionButton
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_imagens_publicas)

        btnCasoDeUso = findViewById(R.id.btnCasoDeUso)

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
            onTornarPublico = { nome -> tornarImagemPrivada(nome) { carregarNomesImagens() }}
        )
        recyclerView.adapter = adapter

        carregarNomesImagens()

        btnCasoDeUso.setOnClickListener {
            explicacoes()
        }
    }

    private fun carregarNomesImagens() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef =
            storage.reference.child("arquivos/$userId/imagensPublicas/") // pasta no Storage

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

    private fun explicacoes() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Caso de Uso - Fotos Públicas")

        val html = """
        🎬 <b>Funcionalidades desta tela:</b><br><br>
        📂 Aqui são exibidos todos as fotos que você enviou e manteve <b>públicas</b> no seu armazenamento.<br><br>

        Os botões disponíveis em cada foto realizam as seguintes ações:<br><br>

        👁️ — Visualizar o vídeo.<br><br>
        🗑️ — Excluir permanentemente.<br><br>
        [share] — Torna a foto <b>privada</b> para outras pessoas.<br><br>
    """.trimIndent()

        val spannable = SpannableStringBuilder(
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        )

        // substitui o marcador [share] pelo ícone real
        val start = spannable.indexOf("[share]")
        if (start != -1) {
            val end = start + "[share]".length
            val drawable = getDrawable(android.R.drawable.ic_menu_share)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            val imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BOTTOM)
            spannable.replace(start, end, " ")
            spannable.setSpan(imageSpan, start, start + 1, 0)
        }

        val textView = TextView(this).apply {
            text = spannable
            setPadding(40, 20, 40, 20)
        }

        builder.setView(textView)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun visualizarImagem(nome: String) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/imagensPublicas/$nome")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Agora sim passamos a URL para o fragment
                val fragment = VisualizarImagemPublicDialogFragment.newInstance(
                    imageUrl = uri.toString(),
                    campoTexto = nome,               // campo informativo
                    imageNameFirebase = nome         // nome salvo no Firebase
                )
                fragment.show(supportFragmentManager, "visualizarImagem")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirImagem(nome: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir imagem")
        builder.setMessage("Tem certeza que deseja excluir a imagem \"$nome\" permanentemente?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
            val storageRef = storage.reference.child("arquivos/$userId/imagensPublicas/$nome")

            // Remove do Storage
            storageRef.delete()
                .addOnSuccessListener {
                    // Remove também do Firestore
                    val firestore = FirebaseFirestore.getInstance()

                    // nomeImagem vem no formato "NomePt_NomeEs.jpg"
                    val nomeSemExtensao = nome.removeSuffix(".jpg")

                    firestore.collection("users")
                        .document(userId)
                        .collection("imagens")
                        .document(nomeSemExtensao)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Imagem excluída com sucesso!", Toast.LENGTH_SHORT)
                                .show()

                            // Remove da lista da RecyclerView
                            listaImagens.remove(nome)
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Erro ao excluir do Firestore: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao excluir do Storage: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun tornarImagemPrivada(nome: String, onComplete: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tornar Pública a Imagem")
        builder.setMessage("Tem certeza que deseja tornar a imagem \"$nome\" privada?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = auth.currentUser?.uid ?: return@setPositiveButton
            val storageRefPrivada =
                storage.reference.child("arquivos/$userId/imagensPrivadas/$nome")
            val storageRefPublica =
                storage.reference.child("arquivos/$userId/imagensPublicas/$nome")

            // Pega os bytes da imagem privada
            storageRefPublica.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                // Sobe para a pasta pública
                storageRefPrivada.putBytes(bytes).addOnSuccessListener {
                    // Apaga a imagem da pasta privada
                    storageRefPublica.delete().addOnSuccessListener {
                        // Atualiza o Firestore, apenas o campo "visualizacao"
                        firestore.collection("users")
                            .document(userId)
                            .collection("imagens")
                            .document(nome.removeSuffix(".jpg"))
                            .update("visualizacao", "privada")
                            .addOnSuccessListener {
                                // Atualiza lista da RecyclerView
                                listaImagens.clear()              // limpa a lista
                                carregarNomesImagens()            // recarrega do Storage

                                Toast.makeText(
                                    this,
                                    "Imagem movida para privada!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Callback para Activity
                                onComplete()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Erro ao atualizar Firestore: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onComplete()
                            }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Erro ao apagar imagem pública: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onComplete()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao enviar para pasta privada: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComplete()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Erro ao ler imagem pública: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onComplete()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}