package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R

class PublicPhotoAdapter(
    private val listaImagensPublicas: MutableList<String>,
    private val onVisualizar: (String) -> Unit,
    private val onExcluir: (String) -> Unit,
    private val onTornarPublico: (String) -> Unit,
): RecyclerView.Adapter<PublicPhotoAdapter.ImagemViewHolder>()
{
    class ImagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeImagem)
        val btnVisualizar: ImageButton = itemView.findViewById(R.id.btnVisualizar)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluir)
        val btnTornarPublico: ImageButton = itemView.findViewById(R.id.btnTornarPrivado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagem_publica, parent, false)
        return ImagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagemViewHolder, position: Int) {
        val nome = listaImagensPublicas[position]
        holder.textViewNome.text = nome

        holder.btnVisualizar.setOnClickListener { onVisualizar(nome) }
        holder.btnExcluir.setOnClickListener { onExcluir(nome) }
        holder.btnTornarPublico.setOnClickListener { onTornarPublico(nome) }
    }

    override fun getItemCount(): Int = listaImagensPublicas.size
}