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

class CreatePhotoStorageViewHolder(activity: Activity)
{
    //layout principal
    val ivPhoto: ImageView = activity.findViewById(R.id.ivPhoto)
    val tvPhotoName: TextView = activity.findViewById(R.id.tvPhotoName)
    val etPhotoName: EditText = activity.findViewById(R.id.etPhotoName)
    val tvPhotoNameEspanhol: TextView = activity.findViewById(R.id.tvPhotoNameEspanhol)
    val etPhotoNameEspanhol: EditText = activity.findViewById(R.id.etPhotoNameEspanhol)
    val btnCasoDeUso: Button = activity.findViewById(R.id.btnCasoDeUso)
    val btnSalvar: Button = activity.findViewById(R.id.btnSalvar)
    val btnCanelar: Button = activity.findViewById(R.id.btnCancelar)

    // Elementos do menu lateral
    var toolbar: Toolbar =  activity.findViewById(R.id.toolbar)
    val drawerLayout: DrawerLayout = activity.findViewById(R.id.drawer_layout)
    val navView: NavigationView = activity.findViewById(R.id.nav_view)
}