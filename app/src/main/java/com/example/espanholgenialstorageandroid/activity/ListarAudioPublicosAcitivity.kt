package com.example.espanholgenialstorageandroid.activity

import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PublicAudioAdapter
import com.example.espanholgenialstorageandroid.fragment.VisualizarAudioPublicoDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarAudioPublicosAcitivity: BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PublicAudioAdapter
    private val listaAudios = mutableListOf<String>()
    private lateinit var selecionarAudioLauncher: ActivityResultLauncher<String>
    private var audioSelecionadaUri: Uri? = null
    private lateinit var btnCasoDeUso: FloatingActionButton
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_audios_privados)

        btnCasoDeUso = findViewById(R.id.btnCasoDeUso)

        // Configura o launcher
        selecionarAudioLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                audioSelecionadaUri = uri
                Toast.makeText(this, "Imagem selecionada!", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Inicializa as views do Drawer e Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.nav_view)

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

        recyclerView = findViewById(R.id.recyclerViewAudios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PublicAudioAdapter(
            listaAudios,
            onVisualizar = { nome -> visualizarAudio(nome) },
            onExcluir = { nome -> excluirAudio(nome) },
            onTornarPublico = { nome -> tornarAudioPublico(nome) { carregarNomesAudios()} }
        )
        recyclerView.adapter = adapter

        carregarNomesAudios()

        btnCasoDeUso.setOnClickListener {
            explicacoes()
        }
    }

    private fun carregarNomesAudios()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPublicos/") // pasta no Storage

        storageRef.listAll()
            .addOnSuccessListener { lista ->
                for (item in lista.items) {
                    listaAudios.add(item.name) // pega só o nome do arquivo
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun explicacoes() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Caso de Uso - Aúdios Públicos")

        val html = """
        🎬 <b>Funcionalidades desta tela:</b><br><br>
        📂 Aqui são exibidos todos os aúdios que você enviou e manteve <b>públicos</b> no seu armazenamento.<br><br>

        Os botões disponíveis em cada aúdio realizam as seguintes ações:<br><br>

        👁️ — Visualizar o aúdio.<br><br>
        🗑️ — Excluir permanentemente.<br><br>
        [share] — Torna o aúdio <b>público</b> para outras pessoas.<br><br>

        💡 Dica: mantenha nomes simples e sem caracteres especiais ao enviar ou editar aúdios.
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

    private fun visualizarAudio(nome: String) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPublicos/$nome")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Abre o DialogFragment para reproduzir o áudio
                val fragment = VisualizarAudioPublicoDialogFragment.newInstance(
                    audioUrl = uri.toString(),
                    campoInformativo = "Nome do áudio:",
                    nomeAudio = nome
                )
                fragment.show(supportFragmentManager, "visualizarAudio")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar áudio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirAudio(nome: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir aúdio")
        builder.setMessage("Tem certeza que deseja excluir o aúdio \"$nome\" permanentemente?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
            val storageRef = storage.reference.child("arquivos/$userId/audiosPublicos/$nome")

            // Remove do Storage
            storageRef.delete()
                .addOnSuccessListener {
                    // Remove também do Firestore
                    val firestore = FirebaseFirestore.getInstance()

                    // nomeImagem vem no formato "NomePt_NomeEs.jpg"
                    val nomeSemExtensao = nome.removeSuffix(".jpg")

                    firestore.collection("users")
                        .document(userId)
                        .collection("audios")
                        .document(nomeSemExtensao)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Imagem excluída com sucesso!", Toast.LENGTH_SHORT)
                                .show()

                            // Remove da lista da RecyclerView
                            listaAudios.remove(nome)
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

    private fun tornarAudioPublico(nome: String, onComplete: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tornar Privado o Aúdio")
        builder.setMessage("Tem certeza que deseja tornar o aúdio \"$nome\" privado?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = auth.currentUser?.uid ?: return@setPositiveButton
            val storageRefPrivada = storage.reference.child("arquivos/$userId/audiosPrivados/$nome")
            val storageRefPublica = storage.reference.child("arquivos/$userId/audiosPublicos/$nome")

            // Pega os bytes da imagem privada
            storageRefPublica.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                // Sobe para a pasta pública
                storageRefPrivada.putBytes(bytes).addOnSuccessListener {
                    // Apaga a imagem da pasta privada
                    storageRefPublica.delete().addOnSuccessListener {
                        // Atualiza o Firestore, apenas o campo "visualizacao"
                        firestore.collection("users")
                            .document(userId)
                            .collection("audios")
                            .document(nome.removeSuffix(".mp3"))
                            .update("visualizacao", "privado")
                            .addOnSuccessListener {
                                // Atualiza lista da RecyclerView
                                listaAudios.clear()              // limpa a lista
                                carregarNomesAudios()            // recarrega do Storage

                                Toast.makeText(
                                    this,
                                    "Audio movido para privado!",
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
                            "Erro ao apagar audio publico: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onComplete()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao enviar para a pasta privada: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComplete()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao ler audio público: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                onComplete()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}