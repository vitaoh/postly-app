package com.victor.postly.adapter

import android.graphics.Bitmap
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.victor.postly.R
import com.victor.postly.databinding.ItemPostBinding
import com.victor.postly.model.Post
import com.victor.postly.utils.Base64Converter
import java.util.Base64.getDecoder

class PostAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            // 1. Avatar do usuário
            if (!post.userPhotoBase64.isNullOrEmpty()) {
                val bitmap = Base64Converter.stringToBitmap(post.userPhotoBase64)
                Glide.with(imgAvatar.context)
                    .load(bitmap)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imgAvatar)
            }

            // 2. Nome e @username
            txtName.text = post.userName ?: "Usuário"
            txtUsername.text = post.userUsername ?: "@anônimo"

            // 3. Conteúdo do post
            txtContent.text = post.description

            // 4. ✅ IMAGEM DO POST (PRINCIPAL!)
            if (!post.imageBase64.isNullOrEmpty()) {
                val postBitmap = Base64Converter.stringToBitmap(post.imageBase64)
                Glide.with(imgPost.context)
                    .load(postBitmap)
                    .centerCrop()
                    .placeholder(R.color.gray_light)  // Cor cinza suave
                    .error(R.drawable.ic_broken_image)
                    .into(imgPost)
                imgPost.visibility = android.view.View.VISIBLE
            } else {
                imgPost.visibility = android.view.View.GONE
            }

            // 5. Chip localização (opcional)
            chipLocation.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}