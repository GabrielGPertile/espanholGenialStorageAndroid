package com.example.espanholgenialstorageandroid.viewHolder

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.espanholgenialstorageandroid.R
import com.google.android.material.textfield.TextInputLayout


/**
 * ViewHolder para gerenciar os elementos de layout da LoginActivity de forma organizada.
 */
class LoginActivityViewHolder(activity: Activity)
{
    // ---- Layout inicial ----
    val initialLayout: LinearLayout = activity.findViewById(R.id.initialLayout)
    val scrollInitialLayout: View = activity.findViewById(R.id.scrollInitialLayout)
    val btnLogar: Button = activity.findViewById(R.id.btnLogar)
    val btnCadastrar: Button = activity.findViewById(R.id.btnCadastrar)

    // ---- Layout de login ----
    val scrollLoginFields: View = activity.findViewById(R.id.scrollLoginFields)
    val loginFields: LinearLayout = activity.findViewById(R.id.loginFields)
    val tvLoginComentario: TextView = activity.findViewById(R.id.tvLoginComentario)
    val tvLoginMail: TextView = activity.findViewById(R.id.tvLoginMail)
    val etLoginMail: EditText = activity.findViewById(R.id.etLoginMail)
    val tvLoginPassword: TextView = activity.findViewById(R.id.tvLoginPassword)
    val passwordInputLayoutLogin: TextInputLayout = activity.findViewById(R.id.passwordInputLayoutLogin)
    val etLoginPassword: EditText = activity.findViewById(R.id.etLoginPassword)
    val btnEntrar: Button = activity.findViewById(R.id.btnEntrar)
    val btnEsqueceuSenha: Button = activity.findViewById(R.id.btnEsqueceuSenha)
    val btnCancelarLogin: Button = activity.findViewById(R.id.btnCancelarLogin)

    // ---- Layout de cadastro ----
    val scrollRegisterFields: View = activity.findViewById(R.id.scrollRegisterFields)
    val registerFields: LinearLayout = activity.findViewById(R.id.registerFields)
    val tvRegistroComentario: TextView = activity.findViewById(R.id.tvRegistroComentario)
    val tvRegisterMail: TextView = activity.findViewById(R.id.tvRegisterMail)
    val etRegisterMail: EditText = activity.findViewById(R.id.etRegisterMail)
    val tvRegisterPassword: TextView = activity.findViewById(R.id.tvRegisterPassword)
    val passwordInputLayoutRegister: TextInputLayout = activity.findViewById(R.id.passwordInputLayoutRegister)
    val etRegisterPassword: EditText = activity.findViewById(R.id.etRegisterPassword)
    val tvRegisterConfirmPassword: TextView = activity.findViewById(R.id.tvRegisterConfirmPassword)
    val passwordConfirmInputLayoutRegister: TextInputLayout = activity.findViewById(R.id.passwordConfirmInputLayoutRegister)
    val etRegisterPasswordConfirm: EditText = activity.findViewById(R.id.etRegisterPasswordConfirm)
    val btnRegistrar: Button = activity.findViewById(R.id.btnRegistrar)
    val btnCancelarRegistro: Button = activity.findViewById(R.id.btnCancelarRegistro)
}