package com.victor.postly.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.victor.postly.model.Post

class PostDao {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    fun addPost(post: Post, callback: (Boolean, String?) -> Unit) {
        post.id = postsCollection.document().id
        postsCollection.document(post.id).set(post)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun getPosts(callback: (List<Post>) -> Unit) {
        postsCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.mapNotNull { doc ->
                    doc.toObject(Post::class.java).copy(id = doc.id)
                }
                callback(posts)
            }
    }
}