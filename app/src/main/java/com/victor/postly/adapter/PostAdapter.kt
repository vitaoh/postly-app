package com.victor.postly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victor.postly.databinding.ItemPostBinding
import com.victor.postly.model.Post
import com.victor.postly.utils.Base64Converter

class PostAdapter(private val posts: MutableList<Post> = mutableListOf()) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val converter = Base64Converter()

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {

            // Descrição do post
            txtContent.text = post.description

            // Imagem do post
            if (!post.image.isNullOrEmpty()) {
                val bitmap = converter.stringToBitmap(post.image)
                imgPost.setImageBitmap(bitmap)
                imgPost.visibility = View.VISIBLE
            } else {
                imgPost.visibility = View.GONE
            }

            // Esconde campos que Post não tem nessa versão simples
            txtName.visibility = View.GONE
            txtUsername.visibility = View.GONE
            imgAvatar.visibility = View.GONE
            chipLocation.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}