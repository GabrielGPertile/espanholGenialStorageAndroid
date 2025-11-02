package com.example.espanholgenialstorageandroid.activity

import com.example.espanholgenialstorageandroid.model.AudioDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SobreNosActivity : BaseDrawerActivity()
{
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var audioDataClass: AudioDataClass
}