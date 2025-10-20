package com.example.espanholgenialstorageandroid.viewHolder

import android.app.Activity
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.espanholgenialstorageandroid.R
import com.google.android.material.navigation.NavigationView

class DashboardActivityViewHolder(activity: Activity)
{
    //layout principal
    val ivInserirAudio: ImageView = activity.findViewById(R.id.ivInserirAudio)
    val ivInserirImagem: ImageView = activity.findViewById(R.id.ivInserirImagem)
    val ivInserirVideos: ImageView = activity.findViewById(R.id.ivInserirVideos)
    val ivInserirFuturasAtualizacoes: ImageView = activity.findViewById(R.id.ivInserirFuturasAtualizacoes)

    // Elementos do menu lateral
    var toolbar: Toolbar =  activity.findViewById(R.id.toolbar)
    val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
    val navView: NavigationView = activity.findViewById(R.id.nav_view)
}