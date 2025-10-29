package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.model.ImageDataClass
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameInterface
import com.example.espanholgenialstorageandroid.strategy.SanitizeFileNameStrategy
import com.example.espanholgenialstorageandroid.viewHolder.CreatePhotoStorageViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore

class CreatePhotoStorageActivity : BaseDrawerActivity() {
    private lateinit var createPhotoStorageViewHolder: CreatePhotoStorageViewHolder
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageDataClass: ImageDataClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_photo_storage)

        createPhotoStorageViewHolder = CreatePhotoStorageViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupDrawer(
            createPhotoStorageViewHolder.drawerLayout,
            createPhotoStorageViewHolder.navView,
            createPhotoStorageViewHolder.toolbar
        )

        loadProfilePhotoInDrawer()

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data

                if (uri != null) {
                    selectedImageUri = uri

                    val bitmap = getCorrectlyOrientedBitmap(uri)

                    if (bitmap != null) {
                        createPhotoStorageViewHolder.ivPhoto.setImageBitmap(bitmap)
                    }
                }
            }
        }

        //configuração dos botões
        createPhotoStorageViewHolder.ivPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

            pickImageLauncher.launch(intent)
        }

        createPhotoStorageViewHolder.btnCasoDeUso.setOnClickListener {
            explicacoes()
        }

        createPhotoStorageViewHolder.btnSalvar.setOnClickListener {
            savePhotoStorage()
        }

        createPhotoStorageViewHolder.btnCanelar.setOnClickListener {
            cancelInsertPhoto()
        }
    }

    fun contarPalavrasCamelCase(texto: String): Int {
        if (texto.isEmpty()) return 0
        // Conta cada letra maiúscula como início de palavra + 1 para a primeira palavra
        val palavras = texto.count { it.isUpperCase() }
        return palavras
    }

    private fun getCorrectlyOrientedBitmap(uri: Uri): Bitmap? {
        val inputSteam = contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputSteam)
        inputSteam.close()

        val exitInputStream = contentResolver.openInputStream(uri)
        val exif = ExifInterface(exitInputStream!!)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        exitInputStream.close()

        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Função para corrigir orientação do Bitmap
    private fun fixBitmapOrientation(bitmap: Bitmap, bytes: ByteArray): Bitmap {
        val exif = ExifInterface(bytes.inputStream())
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun explicacoes() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Caso de Uso")
        builder.setMessage(
            "📌 Cada campo de edição só permite até 3 palavras.\n\n" +
                    "📝 Cada palavra será contabilizada pelas letras maiúsculas.\n\n" +
                    "🌎 Campo Nome da Foto (PT-BR): coloque palavras em português.\n" +
                    "🇪🇸 Campo Foto em Espanhol: coloque palavras em espanhol.\n\n" +
                    "⚠️ Não utilize espaços nos nomes.\n\n" +
                    "💡 Exemplo:\n" +
                    "PT-BR: MaçaVermelha\n" +
                    "ES: ManzanaRojo\n" +
                    "➡️ A imagem será salva como: MaçaVerde_ManzanaVierde.jpg"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun savePhotoStorage() {
        val nomePtSanitizado: String?
        val nomeEsSanitizado: String?

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val nomePt = createPhotoStorageViewHolder.etPhotoName.text.toString().trim()
        val nomeEs = createPhotoStorageViewHolder.etPhotoNameEspanhol.text.toString().trim()

        val storageRef = FirebaseStorage.getInstance().reference

        val regexSemEspacos = Regex("\\s")

        val quantidadePalavraPt = contarPalavrasCamelCase(nomePt)
        val quantidadePalavraEs = contarPalavrasCamelCase(nomeEs)

        val sanitizer: SanitizeFileNameInterface = SanitizeFileNameStrategy()

        if (selectedImageUri == null) {
            Toast.makeText(this, "Selecione uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        if (nomePt.isEmpty() || nomeEs.isEmpty()) {
            Toast.makeText(this, "Preencha os dois nomes", Toast.LENGTH_SHORT).show()
            return
        }

        if(quantidadePalavraEs > 3 || quantidadePalavraPt > 3)
        {
            Toast.makeText(this, "Deve ter menos que 3 palavras cada campo", Toast.LENGTH_SHORT).show()
            return
        }

        if (regexSemEspacos.containsMatchIn(nomePt) || regexSemEspacos.containsMatchIn(nomeEs)) {
            Toast.makeText(this, "Os nomes não podem conter espaços", Toast.LENGTH_SHORT).show()
            return
        }

        val nomePtCapitalizado = nomePt.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val nomeEsCapitalizado = nomeEs.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        try {
            nomePtSanitizado = sanitizer.sanitizeFileName(nomePtCapitalizado)
            nomeEsSanitizado = sanitizer.sanitizeFileName(nomeEsCapitalizado)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

        val imageRef = storageRef.child("arquivos/$userId/imagensPrivadas/${nomePtSanitizado}_${nomeEsSanitizado}.jpg")

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageDataClass = ImageDataClass(
                        nomePt = nomePtCapitalizado,
                        nomeEs = nomeEsCapitalizado,
                        url = uri.toString(),
                        userId = userId
                    )

                    firestore.collection("users")
                        .document(userId)
                        .collection("imagens")
                        .document("${nomePtCapitalizado}_${nomeEsCapitalizado}")
                        .set(imageDataClass)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show()


                            createPhotoStorageViewHolder.ivPhoto.setImageResource(R.drawable.logo_inserir_imagem)
                            createPhotoStorageViewHolder.etPhotoName.text?.clear()
                            createPhotoStorageViewHolder.etPhotoNameEspanhol.text?.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao salvar no banco: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cancelInsertPhoto() {
        /* val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)*/
        finish()
    }
}