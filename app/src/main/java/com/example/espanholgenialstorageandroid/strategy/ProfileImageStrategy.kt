package com.example.espanholgenialstorageandroid.strategy

import android.content.Context
import android.widget.ImageView

interface ProfileImageStrategy {
    fun loadProfileImage(context: Context, imageView: ImageView, userId: String)
}