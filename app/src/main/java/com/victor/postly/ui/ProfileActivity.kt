package com.victor.postly.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.victor.postly.auth.UserAuth
import com.victor.postly.dao.UserDao
import com.victor.postly.databinding.ActivityProfileBinding
import com.victor.postly.model.User
import com.victor.postly.utils.Base64Converter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = UserAuth()
    private val userDao = UserDao()
    private val converter = Base64Converter()

    private var selectedBitmap: Bitmap? = null
    private var currentPhotoBase64: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(contentResolver, uri)
            )
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        selectedBitmap = bitmap
        binding.imgAvatar.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadUserData()
        setupListeners()
    }

    private fun loadUserData() {
        val uid = auth.getCurrentUid() ?: return
        userDao.getUser(uid) { user ->
            user ?: return@getUser
            binding.edtName.setText(user.name)
            binding.edtUsername.setText(user.username)
            binding.edtEmail.setText(user.email)
            currentPhotoBase64 = user.photo

            // Exibe a foto de perfil se existir
            if (!user.photo.isNullOrEmpty()) {
                val bitmap = converter.stringToBitmap(user.photo)
                binding.imgAvatar.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.imgAvatar.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.txtAlterarFoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        binding.btnLogout.setOnClickListener {
            auth.logout()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }

    private fun saveProfile() {
        val name = binding.edtName.text.toString().trim()
        val username = binding.edtUsername.text.toString().trim().lowercase()

        if (name.isEmpty()) {
            binding.tilName.error = "Informe seu nome"
            return
        }
        if (username.isEmpty()) {
            binding.tilUsername.error = "Informe um nome de usuário"
            return
        }

        binding.tilName.error = null
        binding.tilUsername.error = null

        val uid = auth.getCurrentUid() ?: return
        setLoading(true)

        // Usa a nova foto se selecionou uma, senão mantém a atual
        val photoBase64 = selectedBitmap?.let { converter.bitmapToString(it) } ?: currentPhotoBase64

        val updatedUser = User(
            uid = uid,
            name = name,
            username = username,
            email = binding.edtEmail.text.toString().trim(),
            photo = photoBase64
        )

        userDao.save(
            user = updatedUser,
            onSuccess = {
                setLoading(false)
                Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { msg ->
                setLoading(false)
                Toast.makeText(this, "Erro ao salvar: $msg", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnSave.isEnabled = !isLoading
        binding.btnLogout.isEnabled = !isLoading
        binding.btnSave.text = if (isLoading) "Salvando..." else "Salvar"
    }
}