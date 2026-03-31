package com.victor.postly.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.victor.postly.R
import com.victor.postly.auth.UserAuth
import com.victor.postly.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = UserAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (auth.isLoggedIn()) {
            goToHome()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateFields()) loginUser()
        }

        binding.btnCreateUser.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            sendPasswordReset()
        }
    }

    private fun validateFields(): Boolean {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()

        binding.tilEmail.error = null
        binding.tilSenha.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = "Informe seu e-mail"
            return false
        }
        if (senha.isEmpty()) {
            binding.tilSenha.error = "Informe sua senha"
            return false
        }
        return true
    }

    private fun loginUser() {
        val email = binding.edtEmail.text.toString().trim()
        val senha = binding.edtSenha.text.toString()

        setLoading(true)

        auth.login(email, senha) { sucesso, erro ->
            setLoading(false)
            if (sucesso) {
                goToHome()
            } else {
                Toast.makeText(this, erro ?: "Erro ao entrar", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendPasswordReset() {
        val email = binding.edtEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Digite seu e-mail para redefinir a senha"
            return
        }

        auth.sendPasswordReset(email) { sucesso ->
            if (sucesso) {
                Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Erro ao enviar e-mail", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnCreateUser.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Entrando..." else getString(R.string.login)
    }
}