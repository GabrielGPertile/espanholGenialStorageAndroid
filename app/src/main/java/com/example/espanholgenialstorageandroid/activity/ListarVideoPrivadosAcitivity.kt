package com.example.espanholgenialstorageandroid.activity

import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PrivateVideoAdapter
import com.example.espanholgenialstorageandroid.fragment.VisualizarVideoPrivadoDialogFragment
import com.example.espanholgenialstorageandroid.model.VideoDataClass
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView

class ListarVideoPrivadosAcitivity : BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrivateVideoAdapter
    private val listaVideos = mutableListOf<String>()
    private lateinit var selecionarVideoLauncher: ActivityResultLauncher<String>
    private var videoSelecionadoUri: Uri? = null
    private lateinit var btnCasoDeUso: FloatingActionButton
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_videos_privados)

        btnCasoDeUso = findViewById(R.id.btnCasoDeUso)

        // Configura o launcher
        selecionarVideoLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                videoSelecionadoUri = uri
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

        recyclerView = findViewById(R.id.recyclerViewVideos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PrivateVideoAdapter(
            listaVideos,
            onVisualizar = { nome -> visualizarVideo(nome) },
            onEditar = { nome -> editarVideo(nome) },
            onExcluir = { nome ->  excluirVideo(nome) },
            onTornarPublico = { nome ->  tornarVideoPublico(nome) { carregarNomesVideos()}  }
        )
        recyclerView.adapter = adapter

        carregarNomesVideos()

        btnCasoDeUso.setOnClickListener {
            explicacoes()
        }
    }

    private fun carregarNomesVideos()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/videosPrivados/") // pasta no Storage

        storageRef.listAll()
            .addOnSuccessListener { lista ->
                for (item in lista.items) {
                    listaVideos.add(item.name) // pega só o nome do arquivo
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun explicacoes() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Caso de Uso - Vídeos Privados")

        val html = """
        🎬 <b>Funcionalidades desta tela:</b><br><br>
        📂 Aqui são exibidos todos os vídeos que você enviou e manteve <b>privados</b> no seu armazenamento.<br><br>

        Os botões disponíveis em cada vídeo realizam as seguintes ações:<br><br>

        👁️ — Visualizar o vídeo.<br><br>
        ✏️ — Editar ou substituir o arquivo.<br><br>
        🗑️ — Excluir permanentemente.<br><br>
        [share] — Torna o vídeo <b>público</b> para outras pessoas.<br><br>

        💡 Dica: mantenha nomes simples e sem caracteres especiais ao enviar ou editar vídeos.
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

    private fun visualizarVideo(nome: String) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/videosPrivados/$nome")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Cria o dialog fragment passando a URL do vídeo e o nome
                val fragment = VisualizarVideoPrivadoDialogFragment.newInstance(
                    videoUrl = uri.toString(),
                    campoInformativo = "Nome do áudio:",
                    nomeVideo = nome
                )
                fragment.show(supportFragmentManager, "visualizarVideoPrivado")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar vídeo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun editarVideo(nomeAtual: String) {
        val editText = EditText(this).apply { setText(nomeAtual.removeSuffix(".mp4")) }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar nome do vídeo")
            .setView(editText)
            .setPositiveButton("Salvar", null)
            .setNeutralButton("Selecionar novo vídeo", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val novoNomeInput = editText.text.toString().trim()
            if (novoNomeInput.isEmpty()) {
                Toast.makeText(this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()
            val nomeSanitizado = try {
                sanitizer.sanitizeFileName(novoNomeInput)?.lowercase()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } ?: return@setOnClickListener

            val novoNomeFinal = "$nomeSanitizado.mp4"

            if (videoSelecionadoUri != null) {
                substituirVideo(nomeAtual, novoNomeFinal, videoSelecionadoUri!!) {
                    dialog.dismiss()
                }
            } else if (novoNomeFinal != nomeAtual) {
                renomearVideo(nomeAtual, novoNomeFinal) {
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Nada foi alterado", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            selecionarVideoLauncher.launch("video/*")
        }
    }

    // Substitui vídeo antigo por novo arquivo
    private fun substituirVideo(nomeAtual: String, novoNome: String, uri: Uri, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefNovo = storage.reference.child("arquivos/$userId/videosPrivados/$novoNome")
        val storageRefAtual = storage.reference.child("arquivos/$userId/videosPrivados/$nomeAtual")

        storageRefNovo.putFile(uri).addOnSuccessListener {
            storageRefNovo.downloadUrl.addOnSuccessListener { downloadUri ->
                storageRefAtual.delete().addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Erro ao apagar vídeo antigo: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Atualiza Firestore
                    firestore.collection("users")
                        .document(userId)
                        .collection("videos")
                        .document(nomeAtual.removeSuffix(".mp4"))
                        .delete()
                        .addOnSuccessListener {
                            firestore.collection("users")
                                .document(userId)
                                .collection("videos")
                                .document(novoNome.removeSuffix(".mp4"))
                                .set(
                                    VideoDataClass(
                                        nomeVideo = novoNome.removeSuffix(".mp4"),
                                        visualizacao = "privado",
                                        url = downloadUri.toString(),
                                        userId = userId
                                    )
                                ).addOnSuccessListener {
                                    Toast.makeText(this, "Vídeo atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    carregarNomesVideos()
                                    onComplete()
                                }
                        }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao enviar vídeo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Apenas renomeia vídeo existente
    private fun renomearVideo(nomeAtual: String, novoNome: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefOld = storage.reference.child("arquivos/$userId/videosPrivados/$nomeAtual")
        val storageRefNew = storage.reference.child("arquivos/$userId/videosPrivados/$novoNome")

        storageRefOld.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            storageRefNew.putBytes(bytes).addOnSuccessListener {
                storageRefNew.downloadUrl.addOnSuccessListener { downloadUri ->
                    storageRefOld.delete().addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this, "Erro ao apagar vídeo antigo: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        // Atualiza Firestore
                        firestore.collection("users")
                            .document(userId)
                            .collection("videos")
                            .document(nomeAtual.removeSuffix(".mp4"))
                            .delete()
                            .addOnSuccessListener {
                                firestore.collection("users")
                                    .document(userId)
                                    .collection("videos")
                                    .document(novoNome.removeSuffix(".mp4"))
                                    .set(
                                        VideoDataClass(
                                            nomeVideo = novoNome.removeSuffix(".mp4"),
                                            visualizacao = "privado",
                                            url = downloadUri.toString(),
                                            userId = userId
                                        )
                                    ).addOnSuccessListener {
                                        Toast.makeText(this, "Vídeo renomeado com sucesso!", Toast.LENGTH_SHORT).show()
                                        carregarNomesVideos()
                                        onComplete()
                                    }
                            }
                    }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao ler vídeo antigo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun excluirVideo(nome: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir vídeo")
        builder.setMessage("Tem certeza que deseja excluir o vídeo \"$nome\" permanentemente?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
            val storageRef = storage.reference.child("arquivos/$userId/videosPrivados/$nome")

            // Remove do Storage
            storageRef.delete()
                .addOnSuccessListener {
                    val firestore = FirebaseFirestore.getInstance()
                    val nomeSemExtensao = nome.removeSuffix(".mp4")

                    firestore.collection("users")
                        .document(userId)
                        .collection("videos")
                        .document(nomeSemExtensao)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Vídeo excluído com sucesso!", Toast.LENGTH_SHORT).show()

                            // Remove da lista da RecyclerView
                            listaVideos.remove(nome)
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao excluir do Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao excluir do Storage: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun tornarVideoPublico(nome: String, onComplete: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tornar Público o Vídeo")
        builder.setMessage("Tem certeza que deseja tornar o vídeo \"$nome\" público?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()

            val userId = auth.currentUser?.uid ?: return@setPositiveButton
            val storageRefPrivada = storage.reference.child("arquivos/$userId/videosPrivados/$nome")
            val storageRefPublica = storage.reference.child("arquivos/$userId/videosPublicos/$nome")

            // Pega os bytes da imagem privada
            storageRefPrivada.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                // Sobe para a pasta pública
                storageRefPublica.putBytes(bytes).addOnSuccessListener {
                    // Apaga a imagem da pasta privada
                    storageRefPrivada.delete().addOnSuccessListener {
                        // Atualiza o Firestore, apenas o campo "visualizacao"
                        firestore.collection("users")
                            .document(userId)
                            .collection("videos")
                            .document(nome.removeSuffix(".mp4"))
                            .update("visualizacao", "publico")
                            .addOnSuccessListener {
                                // Atualiza lista da RecyclerView
                                listaVideos.clear()              // limpa a lista
                                carregarNomesVideos()            // recarrega do Storage

                                Toast.makeText(
                                    this,
                                    "Vídeo movido para público!",
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
                            "Erro ao apagar vídeo privado: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onComplete()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao enviar para pasta pública: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onComplete()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao ler vídeo privado: ${e.message}", Toast.LENGTH_SHORT)
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