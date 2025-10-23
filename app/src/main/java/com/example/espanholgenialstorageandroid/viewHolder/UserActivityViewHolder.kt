package com.example.espanholgenialstorageandroid.viewHolder

import android.app.Activity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.espanholgenialstorageandroid.R
import com.google.android.material.navigation.NavigationView

class UserActivityViewHolder(activity: Activity)
{
    //layout principal
    val ivPerfilUsuario: ImageView = activity.findViewById(R.id.ivPerfilUsuario)
    val tvNomeCompleto: TextView = activity.findViewById(R.id.tvNomeCompleto)
    val tvNomeCompletoDado: TextView = activity.findViewById(R.id.tvNomeCompletoDado)
    val tvEmail: TextView = activity.findViewById(R.id.tvEmail)
    val tvEmailDado: TextView = activity.findViewById(R.id.tvEmailDado)
    val tvIdade: TextView = activity.findViewById(R.id.tvIdade)
    val tvIdadeDado: TextView = activity.findViewById(R.id.tvIdadeDado)
    val btnEditar: Button = activity.findViewById(R.id.btnEditar)

    // Elementos do menu lateral
    var toolbar: Toolbar =  activity.findViewById(R.id.toolbar)
    val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
    val navView: NavigationView = activity.findViewById(R.id.nav_view)
}