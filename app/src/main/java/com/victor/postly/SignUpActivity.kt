package com.victor.postly

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.postly.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            if (validateFields()) registerUser()
        }

        binding.btnBackLogin.setOnClickListener {
            finish() // Volta para LoginActivity
        }
    }

    private fun validateFields(): Boolean {
        val username = binding.edtUsername.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        // Limpa erros anteriores
        binding.tilUsername.error = null
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        if (username.isEmpty()) {
            binding.tilUsername.error = "Informe um nome de usuário"
            return false
        }
        if (username.length < 3) {
            binding.tilUsername.error = "Mínimo de 3 caracteres"
            return false
        }
        if (name.isEmpty()) {
            binding.tilName.error = "Informe seu nome"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Informe seu e-mail"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Informe uma senha"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Mínimo de 6 caracteres"
            return false
        }
        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = "As senhas não coincidem"
            return false
        }
        return true
    }

    private fun registerUser() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                saveUserToFirestore(uid)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Erro ao cadastrar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserToFirestore(uid: String) {
        val username = binding.edtUsername.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()

        val user = hashMapOf(
            "uid" to uid,
            "username" to username,
            "name" to name,
            "email" to email,
            "createdAt" to Timestamp.now()
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                goToHome()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnBackLogin.isEnabled = !isLoading
        binding.btnRegister.text = if (isLoading) "Aguarde..." else getString(R.string.create_account)
    }
}