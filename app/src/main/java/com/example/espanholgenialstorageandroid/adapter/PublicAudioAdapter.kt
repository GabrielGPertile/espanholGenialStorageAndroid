package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espanholgenialstorageandroid.R

class PublicAudioAdapter(
    private val listaAudiosPrivados: MutableList<String>,
    private val onVisualizar: (String) -> Unit,
    private val onExcluir: (String) -> Unit,
    private val onTornarPublico: (String) -> Unit
): RecyclerView.Adapter<PublicAudioAdapter.AudioViewHolder>()
{
    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeAudio)
        val btnVisualizar: ImageButton = itemView.findViewById(R.id.btnVisualizar)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluir)
        val btnTornarPublico: ImageButton = itemView.findViewById(R.id.btnTornarPublico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio_publico, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int)
    {
        val nome = listaAudiosPrivados[position]
        holder.textViewNome.text = nome

        holder.btnVisualizar.setOnClickListener { onVisualizar(nome) }
        holder.btnExcluir.setOnClickListener { onExcluir(nome) }
        holder.btnTornarPublico.setOnClickListener { onTornarPublico(nome) }
    }

    override fun getItemCount(): Int = listaAudiosPrivados.size
}