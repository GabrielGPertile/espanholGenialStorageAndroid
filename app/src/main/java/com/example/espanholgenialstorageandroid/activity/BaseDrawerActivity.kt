package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.espanholgenialstorageandroid.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

abstract class BaseDrawerActivity : AppCompatActivity()
{
    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navView: NavigationView
    protected lateinit var toggle: ActionBarDrawerToggle

    protected fun setupDrawer(drawerLayout: DrawerLayout, navView: NavigationView, toolbar: androidx.appcompat.widget.Toolbar)
    {
        this.drawerLayout = drawerLayout
        this.navView = navView

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        toggle.drawerArrowDrawable.color = Color.WHITE  // substitua RED pela cor que quiser

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationMenu()
    }

    private fun setupNavigationMenu()
    {
        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_perfil -> {
                    navigateToUserActivity()
                }
                R.id.menu_dashboard_principal -> { navigateToMainDashboard() }
                R.id.menu_dashboard_meusAudiosPrivados -> { navigateToMainPrivateAudioList() }
                R.id.menu_dashboard_meusAudiosPublicos -> { navigateToMainPublicAudioList() }
                R.id.menu_dashboard_meusVideosPrivados -> { navigateToMainPrivateVideoList() }
                R.id.menu_dashboard_minhasFotosPrivadas -> { navigateToMainPrivatePhotoList() }
                R.id.menu_dashboard_minhasFotosPublicas -> { navigateToMainPublicPhotoList() }
                R.id.menu_SobreNos -> {}
                R.id.menu_sair -> {
                    deslogFirebase()
                }
            }

            drawerLayout.closeDrawers()
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

    private fun navigateToUserActivity()
    {
        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMainDashboard()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainPrivateAudioList()
    {
        val intent = Intent(this, ListarAudioPrivadosAcitivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainPublicAudioList()
    {
        val intent = Intent(this, ListarAudioPublicosAcitivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainPrivateVideoList()
    {
        val intent = Intent(this, ListarVideoPrivadosAcitivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainPrivatePhotoList()
    {
        val intent = Intent(this, ListarFotoPrivadasAcitivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainPublicPhotoList()
    {
        val intent = Intent(this, ListarFotoPublicasAcitivity::class.java)
        startActivity(intent)
    }

    protected fun loadProfilePhotoInDrawer()
    {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
        val perfilRef = storageRef.child("arquivos/${user.uid}/perfil/fotodeperfil.jpg")

        // pega o header do NavigationView
        val headerView = navView.getHeaderView(0)
        val ivPerfil = headerView.findViewById<android.widget.ImageView>(R.id.ivPerfil)

        perfilRef.downloadUrl.addOnSuccessListener { uri ->
            com.bumptech.glide.Glide.with(this)
                .load(uri)
                .circleCrop() // deixa circular
                .placeholder(R.drawable.perfil_usuario) // imagem padrão
                .into(ivPerfil)
        }.addOnFailureListener {
            ivPerfil.setImageResource(R.drawable.perfil_usuario)
        }
    }
}