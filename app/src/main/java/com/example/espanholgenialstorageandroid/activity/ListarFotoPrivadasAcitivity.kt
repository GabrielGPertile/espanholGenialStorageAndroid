package com.example.espanholgenialstorageandroid.activity

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.adapter.PrivatePhotoAdapter
import com.example.espanholgenialstorageandroid.fragment.VisualizarImagemPrivadaDialogFragment
import com.example.espanholgenialstorageandroid.model.ImageDataClass
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListarFotoPrivadasAcitivity : BaseDrawerActivity()
{
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrivatePhotoAdapter
    private val listaImagens = mutableListOf<String>()
    private lateinit var selecionarImagemLauncher: ActivityResultLauncher<String>
    private var imagemSelecionadaUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar_imagens_privadas)

        // Configura o launcher
        selecionarImagemLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                imagemSelecionadaUri = uri
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

        recyclerView = findViewById(R.id.recyclerViewImagens)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PrivatePhotoAdapter(
            listaImagens,
            onVisualizar = { nome -> visualizarImagem(nome) },
            onEditar = { nome -> editarImagem(nome) },
            onExcluir = { nome -> excluirImagem(nome) },
            onTornarPublico = { nome -> tornarImagemPublica(nome) { carregarNomesImagens() } }
        )
        recyclerView.adapter = adapter

        carregarNomesImagens()
    }

    private fun carregarNomesImagens()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/imagensPrivadas/") // pasta no Storage

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
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/imagensPrivadas/$nome")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Agora sim passamos a URL para o fragment
                val fragment = VisualizarImagemPrivadaDialogFragment.newInstance(
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

    fun contarPalavrasCamelCase(texto: String): Int {
        if (texto.isEmpty()) return 0
        // Conta cada letra maiúscula como início de palavra + 1 para a primeira palavra
        val palavras = texto.count { it.isUpperCase() }
        return palavras
    }

    private fun editarImagem(nomeAtual: String) {
        val editText = EditText(this).apply { setText(nomeAtual.removeSuffix(".jpg")) } // remove extensão no input

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar nome da imagem")
            .setView(editText)
            .setPositiveButton("Salvar", null)
            .setNeutralButton("Selecionar nova imagem", null)
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
            val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()

            if (regexSemEspacos.containsMatchIn(novoNomeInput)) {
                Toast.makeText(this, "O nome não pode conter espaços", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Verifica se contém e separa pelo "_"
            if (!novoNomeInput.contains("_")) {
                Toast.makeText(this, "O nome deve conter um '_' separando as partes (ex: NomePt_NomeEs)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val partes = novoNomeInput.split("_")
            if (partes.size != 2) {
                Toast.makeText(this, "Use apenas um '_' separando os nomes (ex: NomePt_NomeEs)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nomePt = partes[0]
            val nomeEs = partes[1]

            // 🔹 Função pra contar palavras CamelCase corretamente
            fun contarPalavrasCamelCase(texto: String): Int {
                if (texto.isEmpty()) return 0
                var count = 1
                for (i in 1 until texto.length) {
                    if (texto[i].isUpperCase()) count++
                }
                return count
            }

            val qtdPt = contarPalavrasCamelCase(nomePt)
            val qtdEs = contarPalavrasCamelCase(nomeEs)

            if (qtdPt > 3 || qtdEs > 3) {
                Toast.makeText(this, "Cada parte deve ter no máximo 3 palavras CamelCase", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Capitaliza as duas partes
            val nomePtCapitalizado = nomePt.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            val nomeEsCapitalizado = nomeEs.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

            val nomeFinalBruto = "${nomePtCapitalizado}_${nomeEsCapitalizado}"

            // 🔹 Sanitiza o nome completo
            val nomeSanitizado: String? = try {
                sanitizer.sanitizeFileName(nomeFinalBruto)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nomeSanitizado == null) {
                Toast.makeText(this, "Erro ao sanitizar nome", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoNomeFinal = "$nomeSanitizado.jpg"

            // 🔹 Atualiza a imagem
            if (imagemSelecionadaUri != null) {
                substituirImagem(nomeAtual, novoNomeFinal, imagemSelecionadaUri!!) {
                    dialog.dismiss()
                }
            } else if (novoNomeFinal != nomeAtual) {
                renomearImagem(nomeAtual, novoNomeFinal) {
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Nada foi alterado", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            selecionarImagemLauncher.launch("image/*")
        }
    }

    private fun substituirImagem(nomeAtual: String, novoNome: String, novaUri: Uri, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefAtual = storage.reference.child("arquivos/$userId/imagensPrivadas/$nomeAtual")
        val storageRefNovo = storage.reference.child("arquivos/$userId/imagensPrivadas/$novoNome")

        storageRefNovo.putFile(novaUri).addOnSuccessListener {
            storageRefAtual.delete().addOnSuccessListener {
                firestore.collection("users")
                    .document(userId)
                    .collection("imagens")
                    .document(nomeAtual.removeSuffix(".jpg"))
                    .update("nome", novoNome)
                    .addOnSuccessListener {
                        // Atualiza lista e RecyclerView na Activity
                        listaImagens.remove(nomeAtual)
                        listaImagens.add(novoNome)
                        adapter.notifyDataSetChanged()
                        imagemSelecionadaUri = null
                        Toast.makeText(this, "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()

                        onComplete() // informa que terminou
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao atualizar imagem: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun renomearImagem(nomeAtual: String, novoNome: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefAtual = storage.reference.child("arquivos/$userId/imagensPrivadas/$nomeAtual")
        val storageRefNovo = storage.reference.child("arquivos/$userId/imagensPrivadas/$novoNome")

        storageRefAtual.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            storageRefNovo.putBytes(bytes).addOnSuccessListener {
                storageRefAtual.delete()
                firestore.collection("users")
                    .document(userId)
                    .collection("imagens")
                    .document(nomeAtual.removeSuffix(".jpg"))
                    .update("nome", novoNome)
                    .addOnSuccessListener {
                        listaImagens.remove(nomeAtual)
                        listaImagens.add(novoNome)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Nome atualizado!", Toast.LENGTH_SHORT).show()
                        onComplete()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Erro ao atualizar imagem: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun excluirImagem(nome: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference.child("arquivos/$userId/imagensPrivadas/$nome")

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
                        Toast.makeText(this, "Imagem excluída com sucesso!", Toast.LENGTH_SHORT).show()

                        // Remove da lista da RecyclerView
                        listaImagens.remove(nome)
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

    private fun tornarImagemPublica(nome: String, onComplete: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val storageRefPrivada = storage.reference.child("arquivos/$userId/imagensPrivadas/$nome")
        val storageRefPublica = storage.reference.child("arquivos/$userId/imagensPublicas/$nome")

        // Pega os bytes da imagem privada
        storageRefPrivada.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            // Sobe para a pasta pública
            storageRefPublica.putBytes(bytes).addOnSuccessListener {
                // Apaga a imagem da pasta privada
                storageRefPrivada.delete().addOnSuccessListener {
                    // Atualiza o Firestore, apenas o campo "visualizacao"
                    firestore.collection("users")
                        .document(userId)
                        .collection("imagens")
                        .document(nome.removeSuffix(".jpg"))
                        .update("visualizacao", "publica")
                        .addOnSuccessListener {
                            // Atualiza lista da RecyclerView
                            listaImagens.clear()              // limpa a lista
                            carregarNomesImagens()            // recarrega do Storage

                            Toast.makeText(this, "Imagem movida para pública!", Toast.LENGTH_SHORT).show()

                            // Callback para Activity
                            onComplete()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao atualizar Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                            onComplete()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao apagar imagem privada: ${e.message}", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar para pasta pública: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao ler imagem privada: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete()
        }
    }
}