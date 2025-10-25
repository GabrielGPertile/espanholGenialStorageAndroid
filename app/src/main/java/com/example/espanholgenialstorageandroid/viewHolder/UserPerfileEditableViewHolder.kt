package com.example.espanholgenialstorageandroid.viewHolder

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.espanholgenialstorageandroid.R
import com.google.android.material.navigation.NavigationView

class UserPerfileEditableViewHolder(activity: Activity)
{
    //layout principal
    val ivPerfilUsuario: ImageView = activity.findViewById(R.id.ivPerfilUsuario)
    val tvNomeCompleto: TextView = activity.findViewById(R.id.tvNomeCompleto)
    val etNomeCompletoDado: EditText = activity.findViewById(R.id.etNomeCompletoDado)
    val tvIdade: TextView = activity.findViewById(R.id.tvIdade)
    val etIdadeDado: EditText = activity.findViewById(R.id.etIdadeDado)
    val btnSalvar: Button = activity.findViewById(R.id.btnSalvar)
    val btnCanelar: Button = activity.findViewById(R.id.btnCancelar)

    // Elementos do menu lateral
    var toolbar: Toolbar =  activity.findViewById(R.id.toolbar)
    val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
    val navView: NavigationView = activity.findViewById(R.id.nav_view)
}