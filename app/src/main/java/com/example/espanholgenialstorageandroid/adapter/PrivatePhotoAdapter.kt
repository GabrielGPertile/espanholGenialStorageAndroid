package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R

class PrivatePhotoAdapter(
    private val listaImagensPrivadas: List<String>
): RecyclerView.Adapter<PrivatePhotoAdapter.ImagemViewHolder>()
{
    class ImagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeImagem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagem_privada, parent, false)
        return ImagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagemViewHolder, position: Int) {
        val nome = listaImagensPrivadas[position]
        holder.textViewNome.text = nome
    }

    override fun getItemCount(): Int = listaImagensPrivadas.size
}