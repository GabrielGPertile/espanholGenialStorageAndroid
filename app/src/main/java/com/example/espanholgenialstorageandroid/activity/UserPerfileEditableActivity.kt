package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.bumptech.glide.Glide
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

        userPerfileEditableViewHolder.etNomeCompletoDado.clearFocus()
        userPerfileEditableViewHolder.etIdadeDado.clearFocus()

        loadProfilePhotoInDrawer()
        loadProfilePhoto()

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

                    val bitmap = getCorrectlyOrientedBitmap(uri)

                    if (bitmap != null) {
                        userPerfileEditableViewHolder.ivPerfilUsuario.setImageBitmap(bitmap)
                    }

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

    private fun getCorrectlyOrientedBitmap(uri: Uri): Bitmap?
    {
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

        when (orientation)
        {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun loadProfilePhoto()
    {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = storage.reference
        val perfilRef = storageRef.child("arquivos/$userId/perfil/fotodeperfil.jpg")

        perfilRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this@UserPerfileEditableActivity)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.perfil_usuario)
                .into(userPerfileEditableViewHolder.ivPerfilUsuario)
        }.addOnFailureListener {
            userPerfileEditableViewHolder.ivPerfilUsuario.setImageResource(R.drawable.perfil_usuario)
        }
    } // <- mantém só essa


    // Função para corrigir orientação do Bitmap
    private fun fixBitmapOrientation(bitmap: Bitmap, bytes: ByteArray): Bitmap
    {
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

    // Função para tornar circular (igual antes)
    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        // Determina o tamanho do quadrado
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        // Corta o centro do bitmap
        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = android.graphics.Canvas(output)
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, size, size)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(squaredBitmap, null, rect, paint)

        return output
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