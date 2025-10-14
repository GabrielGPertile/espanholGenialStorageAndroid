package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.ImageViewHolder

class LoadingActivity : AppCompatActivity()
{
    private lateinit var imageViewHolder: ImageViewHolder

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_activity)

        imageViewHolder = ImageViewHolder(this)

        // Definir um atraso de 3 segundos (3000 milissegundos)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)

            // Fechar a LoadingActivity para que o usuário não consiga voltar para ela
            finish()
        },3000)

    }
}