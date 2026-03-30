package com.victor.postly.ui

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.victor.postly.R
import com.victor.postly.dao.PostDao
import com.victor.postly.model.Post
import com.victor.postly.utils.Base64Converter
import java.util.Calendar

class NewPostDialog(private val context: Context) {
    fun show(onPostCreated: () -> Unit) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_new_post)  // Crie esse XML abaixo
        dialog.show()

        val edtDescription = dialog.findViewById<TextInputEditText>(R.id.edtDescription)
        val imgPost = dialog.findViewById<ShapeableImageView>(R.id.imgPost)
        val btnSelectImage = dialog.findViewById<MaterialButton>(R.id.btnSelectImage)
        val btnPostar = dialog.findViewById<MaterialButton>(R.id.btnPostar)

        var selectedImage: Drawable? = null

        btnSelectImage.setOnClickListener {
            // TODO: ImagePicker library ou gallery intent
            // Por agora, simule com drawable
            selectedImage = context.getDrawable(R.drawable.sample_image)  // Adicione um
            imgPost.setImageDrawable(selectedImage)
        }

        btnPostar.setOnClickListener {
            val desc = edtDescription.text.toString().trim()
            if (desc.isEmpty()) {
                Toast.makeText(context, "Digite uma descrição", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = com.victor.postly.auth.UserAuth().getUidUsuarioLogado() ?: return@setOnClickListener
            val timestamp = Calendar.getInstance().timeInMillis

            val imageB64 = selectedImage?.let { Base64Converter.drawableToString(it) }

            val post = Post(
                userId = uid,
                description = desc,
                imageBase64 = imageB64,
                timestamp = timestamp
            )

            PostDao().addPost(post) { success ->
                if (success) {
                    dialog.dismiss()
                    onPostCreated()
                    Toast.makeText(context, "Post criado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}