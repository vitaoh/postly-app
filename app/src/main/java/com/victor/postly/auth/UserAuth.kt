package com.victor.postly.auth

import com.google.firebase.auth.FirebaseAuth

class UserAuth {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, pass: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun signup(email: String, pass: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    fun getUidUsuarioLogado(): String? = auth.currentUser?.uid

    fun getEmailUsuarioLogado(): String? = auth.currentUser?.email

    fun logout() {
        auth.signOut()
    }
}