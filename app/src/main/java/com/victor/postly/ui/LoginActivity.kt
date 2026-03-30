package com.victor.postly.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.victor.postly.R
import com.victor.postly.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()

        // Se já está logado, vai direto pra Home
        if (auth.currentUser != null) {
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

        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                setLoading(false)
                goToHome()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Erro ao entrar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendPasswordReset() {
        val email = binding.edtEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = "Digite seu e-mail para redefinir a senha"
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnCreateUser.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Entrando..." else getString(R.string.login)
    }

}