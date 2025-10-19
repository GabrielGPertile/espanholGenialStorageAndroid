package com.example.espanholgenialstorageandroid.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.espanholgenialstorageandroid.R
import com.example.espanholgenialstorageandroid.viewHolder.ForgetPasswordResetViewHolder
import com.example.espanholgenialstorageandroid.viewHolder.ImageViewHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordReset: AppCompatActivity()
{
    //declaração dos objetos
    private lateinit var imageViewHolder: ImageViewHolder
    private lateinit var forgetPasswordResetViewHolder: ForgetPasswordResetViewHolder
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password_reset)

        //Inicializa os objetos
        imageViewHolder = ImageViewHolder(this)
        forgetPasswordResetViewHolder = ForgetPasswordResetViewHolder(this)

        //Inicializa o Auth do Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Configuração dos Botões
        forgetPasswordResetViewHolder.btnEnviar.setOnClickListener {
            sendPasswordResetEmail()
        }

        forgetPasswordResetViewHolder.btnVoltar.setOnClickListener {
            navigateBackToLogin()
        }
    }

    /**
     * Função responsável por enviar um email de redefinição de senha para o usuário.
     *
     * Objetivo: Validar o email fornecido pelo usuário e, caso seja válido, solicitar ao Firebase Authentication
     * que envie um email para redefinição de senha. Caso o email seja inválido ou ocorra algum erro no envio,
     * uma mensagem de feedback será exibida ao usuário.
     *
     * Entradas:
     * - `email`: String - O endereço de email fornecido pelo usuário no campo de recuperação de senha.
     *
     * Saídas:
     * - Não retorna valores. Exibe mensagens de Toast informando o sucesso ou erro na operação.
     *
     * Caso de uso: Esta função é chamada quando o usuário solicita a redefinição de senha no aplicativo.
     * Ela garante que apenas emails válidos sejam processados e fornece feedback imediato ao usuário.
     *
     * Exemplo de uso:
     * - Entrada válida:
     *   email: "usuario@gmail.com"
     *   Retorno: Toast exibindo "Email de redefinição enviado para usuario@gmail.com"
     * - Entrada inválida (campo vazio):
     *   email: ""
     *   Retorno: Toast exibindo "Insira um email válido."
     * - Entrada inválida (erro no envio):
     *   email: "usuario@gmail.com"
     *   Retorno: Toast exibindo "Erro ao enviar email: [mensagem de erro do Firebase]"
     */
    private fun sendPasswordResetEmail()
    {
        val email = forgetPasswordResetViewHolder.etLoginMail.text.toString().trim()

        if(email.isBlank())
        {
            Toast.makeText(this, "Insira um email válido.", Toast.LENGTH_SHORT).show()
            return
        }

        // Envia email de redefinição
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    Toast.makeText(this, "Email de redefinição enviado para $email", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Erro ao enviar email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }

            }
    }

}