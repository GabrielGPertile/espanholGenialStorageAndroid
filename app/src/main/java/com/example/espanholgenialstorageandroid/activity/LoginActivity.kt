package com.example.espanholgenialstorageandroid.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.ImageViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.LoginActivityViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

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

    /**
     * Função responsável por redirecionar o usuário para a activity de redefinição de senha.
     *
     *  Esta função:
     *  Esta função inicia a Activity `ForgetPasswordReset`, permitindo
     *  que o usuário informe seu e-mail para recuperar a senha.
     *
     * Objetivo:
     * Abrir a tela de redefinição de senha para que o usuário
     * possa informar seu e-mail e recuperar a senha.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Chamado ao clicar no botão "Esqueci minha senha" na tela de login.
     *
     */
    private fun openForgotPasswordActivity()
    {
        val intent = Intent(this, ForgetPasswordReset::class.java)
        startActivity(intent)
    }

    /**
     * Função responsável por exibir o layout inicial e ocultar os outros layouts da tela de forma dinâmica.
     *
     * Objetivo: Tornar o layout inicial visível, enquanto oculta os layouts de login e registro.
     * Isso permite que o usuário retorne à tela inicial, ocultando os outros layouts até que sejam necessários.
     * A função é útil para alternar entre as diferentes telas de login, registro e a tela inicial.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Esta função é acionada quando o usuário interage com um botão de "Voltar" ou qualquer ação que precise retornar à tela inicial.
     * Ao ser chamada, ela torna o layout inicial visível (`View.VISIBLE`), enquanto oculta os layouts de login e registro.
     *
     * Exemplo de uso:
     * - Antes da função ser chamada:
     *   - `scrollLoginFields`: Visível
     *   - `scrollRegisterFields`: Visível
     *   - `scrollInitialLayout`: Oculto
     *
     * - Após a função ser chamada:
     *   - `scrollLoginFields`: Oculto
     *   - `scrollRegisterFields`: Oculto
     *   - `scrollInitialLayout`: Visível
     */
    private fun showInitialScreen() {
        loginActivityViewHolder.scrollLoginFields.visibility = View.GONE
        loginActivityViewHolder.scrollRegisterFields.visibility = View.GONE
        loginActivityViewHolder.scrollInitialLayout.visibility = View.VISIBLE
    }

    /**
     * Função responsável por exibir o layout de cadastro e ocultar os outros
     * layouts da tela de forma dinâmica.
     *
     * Objetivo: Tornar visível o layout de cadastro, enquanto oculta o
     * layout inicial e o de login.
     * Isso permite que o usuário veja a tela de cadastro quando
     * clicar no botão de login na tela inicial,
     * ao mesmo tempo em que mantém os outros layouts ocultos até que sejam necessários.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Esta função é acionada quando o usuário interage com o
     * botão de cadastrar na tela inicial.
     * Ao ser chamada, ela faz com que o layout de cadastrar se
     * torne visível (`View.VISIBLE`), enquanto oculta
     * os layouts de lagoin e inicial, ajustando a interface de acordo com a ação do usuário.
     */
    private fun showRegisterScreen()
    {
        loginActivityViewHolder.scrollLoginFields.visibility = View.GONE
        loginActivityViewHolder.scrollRegisterFields.visibility = View.VISIBLE
        loginActivityViewHolder.scrollInitialLayout.visibility = View.GONE
    }

    /**
     * Função responsável por cancelar o processo de cadastro, limpando os campos do cadastro e
     * retornando à tela inicial.
     *
     * Objetivo: Limpar os campos `etRegisterMail`, `etRegisterPassword` `etRegisterConfirmPassword`,
     * ocultar o layout de cadastro
     * e exibir a tela inicial. Isso é feito quando o usuário clica no botão de cancelar login.
     *
     * Entradas: Não há parâmetros de entrada.
     *
     * Saídas: Não há retorno de valor.
     *
     * Caso de uso: Esta função é acionada quando o usuário interage com o botão de "Cancelar Cadastro"
     * no layout de login. Ela limpa os campos de email, senha e confirmar senhar
     * e alterna a visibilidade dos layouts,
     * ocultando o layout de cadastro e mostrando o layout inicial.
     *
     * Exemplo de uso:
     * - Antes da função ser chamada:
     *   etRegisterMail: "testeCasoUso@gmail.com"
     *   etRegisterPassword: "*****" (senha oculta por segurança)
     *   etRegisterConfirmPassword: "*****" (senha oculta por segurança)
     * - Depois da função ser chamada:
     *   etRegisterMail: ""
     *   etRegisterPassword: "" (senha oculta por segurança)
     *   etRegisterConfirmPassword: "" (senha oculta por segurança)
     */
    private fun cancelRegistrer()
    {
        loginActivityViewHolder.etRegisterMail.setText("")
        loginActivityViewHolder.etRegisterPassword.setText("")
        loginActivityViewHolder.etRegisterPasswordConfirm.setText("")

        loginActivityViewHolder.scrollLoginFields.visibility = View.GONE
        loginActivityViewHolder.scrollRegisterFields.visibility = View.GONE
        loginActivityViewHolder.scrollInitialLayout.visibility = View.VISIBLE
    }

    /**
     * Função responsável por registrar um novo usuário no sistema utilizando o Firebase Authentication.
     *
     * **Objetivo**: Realizar o cadastro do usuário utilizando o email e senha fornecidos, verificando se a confirmação da senha corresponde à senha inicial.
     * Caso o cadastro seja bem-sucedido, uma mensagem de boas-vindas é exibida. Se ocorrer algum erro, uma mensagem de erro personalizada é apresentada.
     * Após o processo, os campos de registro são limpos e o layout retorna à tela inicial.
     *
     * **Entradas**:
     * - `emailRegistrar`: String - O endereço de email fornecido pelo usuário.
     * - `passwordRegistrar`: String - A senha fornecida pelo usuário.
     * - `passwordConfirmRegistrar`: String - A confirmação da senha fornecida pelo usuário.
     *
     * **Saídas**:
     * - Não há retorno direto, mas exibe mensagens via `Toast` para indicar o resultado da operação.
     *
     * **Caso de uso**: Esta função é chamada durante o processo de registro, quando o usuário tenta se cadastrar com um novo email e senha.
     * Ela valida se a senha e a confirmação da senha são iguais, realiza o cadastro no Firebase e exibe o feedback adequado para o usuário.
     *
     * **Exemplo de uso**:
     * - Entrada válida:
     *   - `emailRegistrar`: "usuario@gmail.com"
     *   - `passwordRegistrar`: "senha123"
     *   - `passwordConfirmRegistrar`: "senha123"
     *   - Resultado: "Registro bem-sucedido! Bem-vindo, usuario@gmail.com"
     *
     * - Entrada inválida (email já cadastrado):
     *   - `emailRegistrar`: "usuario@gmail.com"
     *   - `passwordRegistrar`: "senha123"
     *   - `passwordConfirmRegistrar`: "senha123"
     *   - Resultado: "Erro ao registrar: Esse email já está cadastrado."
     *
     * - Entrada inválida (senha fraca):
     *   - `emailRegistrar`: "usuario@gmail.com"
     *   - `passwordRegistrar`: "123"
     *   - `passwordConfirmRegistrar`: "123"
     *   - Resultado: "Erro ao registrar: A senha é muito fraca."
     *
     * - Entrada inválida (Confirmar senha diferente da Senha):
     *   - `emailRegistrar`: "usuario@gmail.com"
     *   - `passwordRegistrar`: "senha123"
     *   - `passwordConfirmRegistrar`: "senha1234"
     *   - Resultado: "Erro ao registrar: A senha digitada está diferente do confirmar senha."
     */
    private fun registerUser(emailRegistrar: String, passwordRegistrar: String, passwordConfirmRegistrar: String) {
        if(passwordRegistrar == passwordConfirmRegistrar) {
            auth.createUserWithEmailAndPassword(emailRegistrar, passwordRegistrar)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Exibe mensagem de sucesso
                        Toast.makeText(
                            this,
                            "Registro bem-sucedido! Bem-vindo, ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Exibe mensagem de erro personalizada
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> "A senha é muito fraca."
                            is FirebaseAuthInvalidCredentialsException -> "O email é inválido."
                            is FirebaseAuthUserCollisionException -> "Esse email já está cadastrado."
                            else -> "Erro desconhecido. Tente novamente."
                        }

                        Toast.makeText(this, "Erro ao registrar: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }

            // Redefine os campos e retorna para o layout inicial
            loginActivityViewHolder.etRegisterMail.setText("")
            loginActivityViewHolder.etRegisterPassword.setText("")
            loginActivityViewHolder.etRegisterPasswordConfirm.setText("")

            // Exibe o layout inicial
            showInitialScreen()
        } else {
            // Exibe mensagem de erro caso as senhas não correspondam
            Toast.makeText(this, "Erro ao registrar: A senha digitada está diferente do confirmar senha.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Autentica o usuário utilizando Firebase Authentication com base no email e senha fornecidos.
     *
     * Objetivo: Verificar as credenciais fornecidas pelo usuário e realizar o login. Em caso de sucesso,
     * exibe uma mensagem de boas-vindas com o email do usuário e navega para a tela principal. Em caso de falha,
     * exibe uma mensagem de erro com detalhes sobre o problema.
     *
     * Entradas:
     * - `emailLogin`: String - O email inserido pelo usuário. Qualquer espaço em branco será removido com `trim()`.
     * - `passwordLogin`: String - A senha inserida pelo usuário. Qualquer espaço em branco será removido com `trim()`.
     *
     * Saídas:
     * - Não há retorno explícito, mas a função apresenta mensagens ao usuário via `Toast` para indicar o
     * sucesso ou falha da operação. Além disso, em caso de sucesso, redireciona o usuário para a tela principal.
     *
     * Caso de uso: A função é chamada quando o usuário pressiona o botão de login na interface.
     * Ela utiliza os valores inseridos para autenticar o usuário no Firebase.
     *
     * Fluxo de execução:
     * 1. Os parâmetros `emailLogin` e `passwordLogin` são ajustados usando `trim()` para remover espaços indesejados.
     * 2. O Firebase Authentication tenta autenticar o usuário com as credenciais fornecidas.
     * 3. Se a autenticação for bem-sucedida:
     *    - Exibe uma mensagem de boas-vindas com o email do usuário.
     *    - Navega para a tela principal usando `navigateToDashboardActivity()`.
     * 4. Se a autenticação falhar:
     *    - Exibe uma mensagem de erro detalhando o motivo da falha.
     *
     * Exemplo de uso:
     * - Entrada válida:
     *   - emailLogin: "usuario@exemplo.com "
     *   - passwordLogin: " senha123"
     *   (com `trim()` aplicado: emailLogin: "usuario@exemplo.com", passwordLogin: "senha123")
     *   Resultado: "Login bem-sucedido! Bem-vindo, usuario@exemplo.com"
     * - Entrada inválida:
     *   - emailLogin: "usuario@exemplo.com"
     *   - passwordLogin: "senhaerrada"
     *   Resultado: "Erro ao fazer login: [mensagem de erro do Firebase]"
     */
    private fun loginUser(emailLogin: String, passwordLogin: String) {
        // Removendo espaços desnecessários das entradas
        val trimmedEmail = emailLogin.trim()
        val trimmedPassword = passwordLogin.trim()

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(
                        this,
                        "Login bem-sucedido! Bem-vindo, ${user?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToDashboardActivity() // Navega para a tela principal após o login bem-sucedido
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao fazer login: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /**
     * Função responsável por validar as entradas de email e senha no processo de login.
     *
     * Objetivo: Verificar se o email fornecido está em um formato válido e se a senha atende aos critérios mínimos.
     * Caso as validações falhem, uma mensagem de erro será exibida ao usuário.
     *
     * Entradas:
     * - `email`: String - O endereço de email fornecido pelo usuário.
     * - `password`: String - A senha fornecida pelo usuário.
     *
     * Saídas:
     * - Boolean: Retorna `true` se os dados forem válidos, ou `false` caso contrário.
     *
     * Caso de uso: Esta função é chamada durante o processo de login, quando o usuário tenta se autenticar.
     * Ela valida os dados fornecidos antes de prosseguir com o login.
     *
     * Exemplo de uso:
     * - Entrada válida:
     *   email: "usuario@gmail.com"
     *   password: "senha123"
     *   Retorno: `true`
     * - Entrada inválida (email incorreto):
     *   email: "usuario@gmail"
     *   password: "senha123"
     *   Retorno: `false` (com mensagem: "Insira um email válido.")
     * - Entrada inválida (senha muito curta):
     *   email: "usuario@gmail.com"
     *   password: "123"
     *   Retorno: `false` (com mensagem: "A senha deve ter pelo menos 6 caracteres.")
     */
    private fun validateLoginInput(email: String, password: String): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Insira um email válido.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isBlank() || password.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * Valida as entradas de email, senha e confirmação de senha no processo de cadastro.
     *
     * **Objetivo**: Garantir que o email esteja em um formato válido e que a senha e a confirmação de senha atendam aos critérios mínimos.
     * Se qualquer validação falhar, uma mensagem de erro será exibida ao usuário.
     *
     * **Entradas**:
     * - `email`: String - O endereço de email fornecido pelo usuário. Deve estar em um formato válido.
     * - `password`: String - A senha fornecida pelo usuário. Deve ter pelo menos 6 caracteres.
     * - `confirmPassword`: String - A confirmação da senha fornecida pelo usuário. Deve corresponder aos critérios da senha.
     *
     * **Saídas**:
     * - `Boolean`: Retorna `true` se todos os dados forem válidos; caso contrário, retorna `false` e exibe uma mensagem de erro apropriada.
     *
     * **Caso de uso**: Esta função é acionada durante o processo de cadastro, quando o usuário tenta se registrar.
     * Antes de enviar os dados ao servidor, a função verifica se as entradas atendem aos critérios necessários.
     *
     * **Exemplo de uso**:
     * - Entrada válida:
     *   - email: "usuario@gmail.com"
     *   - password: "senha123"
     *   - confirmPassword: "senha123"
     *   - Retorno: `true`
     *
     * - Entrada inválida (email incorreto):
     *   - email: "usuario@gmail"
     *   - password: "senha123"
     *   - confirmPassword: "senha123"
     *   - Retorno: `false` (com mensagem: "Insira um email válido.")
     *
     * - Entrada inválida (senha muito curta):
     *   - email: "usuario@gmail.com"
     *   - password: "123"
     *   - confirmPassword: "123"
     *   - Retorno: `false` (com mensagem: "A senha deve ter pelo menos 6 caracteres.")
     */
    private fun validateRegisterInput(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Insira um email válido.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isBlank() || password.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPassword.isBlank() || confirmPassword.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * Navega para a tela principal do aplicativo (DashboardActivity).
     *
     * Objetivo: Iniciar a `DashboardActivity` e redirecionar o usuário para a tela principal do aplicativo.
     * Essa função é chamada geralmente após o sucesso de uma ação, como login ou registro.
     *
     * Entradas:
     * - Não há parâmetros de entrada.
     *
     * Saídas:
     * - Não há valor de retorno. A função inicia a `DashboardActivity` usando uma `Intent`.
     *
     * Caso de uso:
     * - A função é acionada quando o usuário conclui uma ação importante, como autenticação bem-sucedida ou
     *   conclusão de cadastro. Ela redireciona o fluxo da aplicação para a tela principal.
     *
     * Exemplo de uso:
     * - Após um login bem-sucedido:
     *   `kotlin
     *   if (task.isSuccessful) {
     *       navigateToDashboardActivity()
     *   }
     *   ```
     *
     * - Após o registro de um novo usuário:
     *   ```kotlin
     *   navigateToDashboardActivity()
     *   ```
     */
    private fun navigateToDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

}