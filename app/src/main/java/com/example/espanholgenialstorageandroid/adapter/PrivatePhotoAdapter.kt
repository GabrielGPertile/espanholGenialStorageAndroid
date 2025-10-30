package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R

class PrivatePhotoAdapter(
    private val listaImagensPrivadas: MutableList<String>,
    private val onVisualizar: (String) -> Unit,
    private val onEditar: (String) -> Unit,
    private val onExcluir: (String) -> Unit,
    private val onTornarPublico: (String) -> Unit,
): RecyclerView.Adapter<PrivatePhotoAdapter.ImagemViewHolder>()
{
    class ImagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeImagem)
        val btnVisualizar: ImageButton = itemView.findViewById(R.id.btnVisualizar)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluir)
        val btnTornarPublico: ImageButton = itemView.findViewById(R.id.btnTornarPublico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagem_privada, parent, false)
        return ImagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagemViewHolder, position: Int) {
        val nome = listaImagensPrivadas[position]
        holder.textViewNome.text = nome

        holder.btnVisualizar.setOnClickListener { onVisualizar(nome) }
        holder.btnEditar.setOnClickListener { onEditar(nome) }
        holder.btnExcluir.setOnClickListener { onExcluir(nome) }
        holder.btnTornarPublico.setOnClickListener { onTornarPublico(nome) }

    }

    override fun getItemCount(): Int = listaImagensPrivadas.size
}