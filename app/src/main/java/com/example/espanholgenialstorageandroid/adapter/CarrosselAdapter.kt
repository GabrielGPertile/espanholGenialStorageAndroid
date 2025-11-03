package com.example.espanholgenialstorageandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.espanholgenialstorageandroid.R

data class CarrosselItem(val imageUrl: String, val descricao: String)

class CarrosselAdapter(private val lista: List<CarrosselItem>) :
    RecyclerView.Adapter<CarrosselAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagem: ImageView = itemView.findViewById(R.id.imageViewCarrossel)
        val texto: TextView = itemView.findViewById(R.id.textViewDescricaoCarrossel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrossel_sobre_nos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.perfil_usuario)
            .into(holder.imagem)

        holder.texto.text = item.descricao
    }

    override fun getItemCount(): Int = lista.size
}