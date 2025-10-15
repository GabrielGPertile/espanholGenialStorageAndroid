package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import android.view.View
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

    /**
     * Função responsável por exibir o layout de login e ocultar os outros
     * layouts da tela de forma dinâmica.
     *
     * Objetivo: Tornar visível o layout de login, enquanto oculta o
     * layout inicial e o de registro.
     * Isso permite que o usuário veja a tela de login quando
     * clicar no botão de login na tela inicial,
     * ao mesmo tempo em que mantém os outros layouts ocultos até que sejam necessários.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Esta função é acionada quando o usuário interage com o
     * botão de login na tela inicial.
     * Ao ser chamada, ela faz com que o layout de login se
     * torne visível (`View.VISIBLE`), enquanto oculta
     * os layouts de registro e inicial, ajustando a interface de acordo com a ação do usuário.
     */
    private fun showLoginScreen() {
        loginActivityViewHolder.scrollLoginFields.visibility = View.VISIBLE
        loginActivityViewHolder.scrollRegisterFields.visibility = View.GONE
        loginActivityViewHolder.scrollInitialLayout.visibility = View.GONE
    }

    /**
     * Função responsável por cancelar o processo de login, limpando os campos de login e
     * retornando à tela inicial.
     *
     * Objetivo: Limpar os campos `etLoginMail` e `etLoginPassword`, ocultar o layout de login
     * e exibir a tela inicial. Isso é feito quando o usuário clica no botão de cancelar login.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Esta função é acionada quando o usuário interage com o botão de "Cancelar Login"
     * no layout de login. Ela limpa os campos de email e senha e alterna a visibilidade dos layouts,
     * ocultando o layout de login e mostrando o layout inicial.
     *
     * Exemplo de uso:
     * - Antes da função ser chamada:
     *   etLoginMail: "testeCasoUso@gmail.com"
     *   etLoginPassword: "*****" (senha oculta por segurança)
     * - Depois da função ser chamada:
     *   etLoginMail: ""
     *   etLoginPassword: ""
     *   Layout de login oculto e layout inicial visível.
     */
    private fun cancelLogin() {
        loginActivityViewHolder.etLoginMail.setText("")
        loginActivityViewHolder.etLoginPassword.setText("")

        loginActivityViewHolder.scrollLoginFields.visibility = View.GONE
        loginActivityViewHolder.scrollRegisterFields.visibility = View.GONE
        loginActivityViewHolder.scrollInitialLayout.visibility = View.VISIBLE
    }
}