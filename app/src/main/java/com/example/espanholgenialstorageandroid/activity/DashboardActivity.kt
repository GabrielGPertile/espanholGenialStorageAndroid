package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.DashboardActivityViewHolder
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity: AppCompatActivity()
{
    private lateinit var dashboardActivityViewHolder: DashboardActivityViewHolder
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        dashboardActivityViewHolder = DashboardActivityViewHolder(this)

        toggle = ActionBarDrawerToggle(
            this,                                      // Activity
            dashboardActivityViewHolder.drawerLayout,  // DrawerLayout
            dashboardActivityViewHolder.toolbar,       // Toolbar
            R.string.navigation_drawer_open,           // texto abrir
            R.string.navigation_drawer_close           // texto fechar
        )


        dashboardActivityViewHolder.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Lida com cliques no menu lateral
        dashboardActivityViewHolder.navView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_perfil -> { /* abrir perfil */ }
                R.id.menu_configuracoes -> { /* abrir configurações */ }
                R.id.menu_sair -> {
                    deslogFirebase()
                }
            }

            dashboardActivityViewHolder.drawerLayout.closeDrawers()
            true
        }

        // Exemplo de clique nas imagens
        dashboardActivityViewHolder.ivInserirAudio.setOnClickListener {
            Toast.makeText(this, "Abrir Inserir Áudio", Toast.LENGTH_SHORT).show()
        }
    }
}