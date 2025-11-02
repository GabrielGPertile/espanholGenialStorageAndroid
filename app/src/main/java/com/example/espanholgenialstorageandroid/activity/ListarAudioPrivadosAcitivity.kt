package com.example.espanholgenialstorageandroid.activity

import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PrivateAudioAdapter
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.example.espanholgenialstorageandroid.fragment.VisualizarAudioPrivadoDialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarAudioPrivadosAcitivity : BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrivateAudioAdapter
    private val listaAudios = mutableListOf<String>()
    private lateinit var selecionarAudioLauncher: ActivityResultLauncher<String>
    private var audioSelecionadaUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_audios_privados)

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
        adapter = PrivateAudioAdapter(
            listaAudios,
            onVisualizar = { nome -> visualizarAudio(nome) },
            onEditar = { nome -> editarAudio(nome) },
            onExcluir = { nome -> excluirAudio(nome) },
            onTornarPublico = { nome -> tornarAudioPublico(nome) { carregarNomesAudios()} }
        )
        recyclerView.adapter = adapter

        carregarNomesAudios()
    }

    private fun carregarNomesAudios()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPrivados/") // pasta no Storage

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

    private fun visualizarAudio(nome: String) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPrivados/$nome")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Abre o DialogFragment para reproduzir o áudio
                val fragment = VisualizarAudioPrivadoDialogFragment.newInstance(
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

    private fun editarAudio(nomeAtual: String) {
        val editText = EditText(this).apply { setText(nomeAtual.removeSuffix(".mp3")) }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar nome do áudio")
            .setView(editText)
            .setPositiveButton("Salvar", null)
            .setNeutralButton("Selecionar novo áudio", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val novoNomeInput = editText.text.toString().trim()
            if (novoNomeInput.isEmpty()) {
                Toast.makeText(this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val regexSemEspacos = Regex("\\s")
            if (regexSemEspacos.containsMatchIn(novoNomeInput)) {
                Toast.makeText(this, "O nome não pode conter espaços", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Sanitiza o nome completo
            val nomeSanitizado: String? = try {
                sanitizer.sanitizeFileName(novoNomeInput)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nomeSanitizado == null) {
                Toast.makeText(this, "Erro ao sanitizar nome", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Se o nome mudou, renomeia
            val novoNomeFinal = "$novoNomeInput.mp3"

            if (audioSelecionadaUri != null) {
                // Substitui pelo novo áudio selecionado
                substituirAudio(nomeAtual, novoNomeFinal, audioSelecionadaUri!!) {
                    dialog.dismiss()
                }
            } else if (novoNomeFinal != nomeAtual) {
                // Apenas renomeia no Storage/Firestore
                renomearAudio(nomeAtual, novoNomeFinal) {
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Nada foi alterado", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            selecionarAudioLauncher.launch("audio/*")
        }
    }

    private fun substituirAudio(nomeAtual: String, novoNome: String, uri: Uri, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPrivados/$novoNome")

        storageRef.putFile(uri).addOnSuccessListener {
            // Apaga o antigo
            storage.reference.child("arquivos/$userId/audiosPrivados/$nomeAtual").delete()
            // Atualiza Firestore
            firestore.collection("users")
                .document(userId)
                .collection("audios")
                .document(nomeAtual.removeSuffix(".mp3"))
                .delete()
            firestore.collection("users")
                .document(userId)
                .collection("audios")
                .document(novoNome.removeSuffix(".mp3"))
                .set(mapOf("visualizacao" to "privado"))
            listaAudios.clear()
            carregarNomesAudios()
            onComplete()
        }
    }

    private fun renomearAudio(nomeAtual: String, novoNome: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefOld = storage.reference.child("arquivos/$userId/audiosPrivados/$nomeAtual")
        val storageRefNew = storage.reference.child("arquivos/$userId/audiosPrivados/$novoNome")

        // Copia bytes do antigo
        storageRefOld.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            storageRefNew.putBytes(bytes).addOnSuccessListener {
                storageRefOld.delete()
                firestore.collection("users")
                    .document(userId)
                    .collection("audios")
                    .document(nomeAtual.removeSuffix(".mp3"))
                    .delete()
                firestore.collection("users")
                    .document(userId)
                    .collection("audios")
                    .document(novoNome.removeSuffix(".mp3"))
                    .set(mapOf("visualizacao" to "privado"))
                listaAudios.clear()
                carregarNomesAudios()
                onComplete()
            }
        }
    }

    private fun excluirAudio(nome: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/audiosPrivados/$nome")

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
                        Toast.makeText(this, "Imagem excluída com sucesso!", Toast.LENGTH_SHORT).show()

                        // Remove da lista da RecyclerView
                        listaAudios.remove(nome)
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

    private fun tornarAudioPublico(nome: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefPrivada = storage.reference.child("arquivos/$userId/audiosPrivados/$nome")
        val storageRefPublica = storage.reference.child("arquivos/$userId/audiosPublicos/$nome")

        // Pega os bytes da imagem privada
        storageRefPrivada.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            // Sobe para a pasta pública
            storageRefPublica.putBytes(bytes).addOnSuccessListener {
                // Apaga a imagem da pasta privada
                storageRefPrivada.delete().addOnSuccessListener {
                    // Atualiza o Firestore, apenas o campo "visualizacao"
                    firestore.collection("users")
                        .document(userId)
                        .collection("audios")
                        .document(nome.removeSuffix(".mp3"))
                        .update("visualizacao", "publico")
                        .addOnSuccessListener {
                            // Atualiza lista da RecyclerView
                            listaAudios.clear()              // limpa a lista
                            carregarNomesAudios()            // recarrega do Storage

                            Toast.makeText(this, "Audio movido para público!", Toast.LENGTH_SHORT).show()

                            // Callback para Activity
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao atualizar Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                            onComplete()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao apagar audio privado: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar para pasta pública: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao ler audio privado: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }
}