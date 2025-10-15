package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.ImageViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.LoginActivityViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity()
{
    //declaração dos objetos
    private lateinit var imageViewHolder: ImageViewHolder
    private lateinit var loginActivityViewHolder: LoginActivityViewHolder
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //Inicializa os objetos
        imageViewHolder = ImageViewHolder(this)
        loginActivityViewHolder = LoginActivityViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Configuração dos Botões
        loginActivityViewHolder.btnLogar.setOnClickListener {
            showLoginScreen()
        }

        loginActivityViewHolder.btnCancelarLogin.setOnClickListener {
            cancelLogin()
        }

        loginActivityViewHolder.btnCadastrar.setOnClickListener {
            showRegisterScreen()
        }

        loginActivityViewHolder.btnCancelarRegistro.setOnClickListener {
            cancelRegistrer()
        }

        loginActivityViewHolder.btnEntrar.setOnClickListener {
            val emailLogin = loginActivityViewHolder.etLoginMail.text.toString()
            val passwordLogin = loginActivityViewHolder.etLoginPassword.text.toString()

            if(validateLoginInput(emailLogin, passwordLogin))
            {
                loginUser(emailLogin, passwordLogin)
            }
        }

        loginActivityViewHolder.btnRegistrar.setOnClickListener {
            val emailRegistrar = loginActivityViewHolder.etRegisterMail.text.toString()
            val passwordRegistrar = loginActivityViewHolder.etRegisterPassword.text.toString()
            val passwordConfirmRegistrar = loginActivityViewHolder.etRegisterPasswordConfirm.text.toString()

            if (validateRegisterInput(emailRegistrar, passwordRegistrar, passwordConfirmRegistrar)) {
                registerUser(emailRegistrar, passwordRegistrar, passwordConfirmRegistrar)
            }
        }

        loginActivityViewHolder.btnEsqueceuSenha.setOnClickListener {
            openForgotPasswordActivity()
        }
    }
}