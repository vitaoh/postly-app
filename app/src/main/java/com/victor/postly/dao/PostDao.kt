package com.victor.postly.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.victor.postly.model.Post

class PostDao {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("posts")

    fun addPost(
        post: Post,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        collection.add(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao criar post") }
    }

    fun getPosts(onResult: (List<Post>) -> Unit) {
        collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.mapNotNull { it.toObject(Post::class.java) }
                onResult(posts)
            }
            .addOnFailureListener { e ->
                Log.e("PostDao", "Erro ao buscar posts: ${e.message}")
                onResult(emptyList())
            }
    }
}