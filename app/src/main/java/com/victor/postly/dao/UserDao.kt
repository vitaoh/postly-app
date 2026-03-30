package com.victor.postly.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.victor.postly.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserDao {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUser(uid: String): User? = suspendCoroutine { cont ->
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    cont.resume(doc.toObject(User::class.java))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { cont.resume(null) }
    }
}