package com.victor.postly.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.victor.postly.auth.UserAuth
import com.victor.postly.dao.PostDao
import com.victor.postly.databinding.DialogNewPostBinding
import com.victor.postly.model.Post
import com.victor.postly.utils.Base64Converter

class NewPostDialog : DialogFragment() {

    private var _binding: DialogNewPostBinding? = null
    private val binding get() = _binding!!

    var onPostCreated: (() -> Unit)? = null

    private var selectedBitmap: Bitmap? = null
    private val converter = Base64Converter()
    private val auth = UserAuth()

    // Abre a galeria e recebe a imagem selecionada
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(requireContext().contentResolver, uri)
            )
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        }
        selectedBitmap = bitmap
        binding.imgPost.setImageBitmap(bitmap)
        binding.imgPost.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnPostar.setOnClickListener {
            createPost()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createPost() {
        val description = binding.edtDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(context, "Digite uma descrição", Toast.LENGTH_SHORT).show()
            return
        }

        if (auth.getCurrentUid() == null) {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val imageBase64 = selectedBitmap?.let { converter.bitmapToString(it) }

        val post = Post(
            description = description,
            image = imageBase64
        )

        PostDao().addPost(
            post = post,
            onSuccess = {
                setLoading(false)
                Toast.makeText(context, "Post criado!", Toast.LENGTH_SHORT).show()
                onPostCreated?.invoke()
                dismiss()
            },
            onError = { msg ->
                setLoading(false)
                Toast.makeText(context, "Erro: $msg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnPostar.isEnabled = !isLoading
        binding.btnSelectImage.isEnabled = !isLoading
        binding.btnPostar.text = if (isLoading) "Postando..." else "Postar"
    }
}