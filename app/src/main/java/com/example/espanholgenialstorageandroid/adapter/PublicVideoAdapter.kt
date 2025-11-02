package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R

class PublicVideoAdapter(
    private val listaVideosPrivados: MutableList<String>,
    private val onVisualizar: (String) -> Unit,
    private val onExcluir: (String) -> Unit,
    private val onTornarPrivado: (String) -> Unit,
): RecyclerView.Adapter<PublicVideoAdapter.VideoViewHolder>()
{
    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeVideo)
        val btnVisualizar: ImageButton = itemView.findViewById(R.id.btnVisualizar)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluir)
        val btnTornarPrivado: ImageButton = itemView.findViewById(R.id.btnTornarPrivado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_publico, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val nome = listaVideosPrivados[position]
        holder.textViewNome.text = nome

        holder.btnVisualizar.setOnClickListener { onVisualizar(nome) }
        holder.btnExcluir.setOnClickListener { onExcluir(nome) }
        holder.btnTornarPrivado.setOnClickListener { onTornarPrivado(nome) }
    }

    override fun getItemCount(): Int = listaVideosPrivados.size
}