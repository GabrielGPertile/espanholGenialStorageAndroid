package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.UserActivityViewHolder
import com.google.firebase.auth.FirebaseAuth

class UserActivity: AppCompatActivity()
{
    private lateinit var userActivityViewHolder: UserActivityViewHolder
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)

        userActivityViewHolder = UserActivityViewHolder(this)

        toggle = ActionBarDrawerToggle(
            this,                                      // Activity
            userActivityViewHolder.drawerLayout,  // DrawerLayout
            userActivityViewHolder.toolbar,       // Toolbar
            R.string.navigation_drawer_open,           // texto abrir
            R.string.navigation_drawer_close           // texto fechar
        )

        userActivityViewHolder.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Lida com cliques no menu lateral
        userActivityViewHolder.navView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_perfil -> { /* abrir perfil */ }
                R.id.menu_configuracoes -> { /* abrir configurações */ }
                R.id.menu_sair -> {
                    deslogFirebase()
                }
            }

            userActivityViewHolder.drawerLayout.closeDrawers()
            true
        }
    }

    private fun deslogFirebase()
    {
        // desloga do Firebase
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}