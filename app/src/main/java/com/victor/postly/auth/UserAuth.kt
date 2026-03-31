package com.victor.postly.auth

import com.google.firebase.auth.FirebaseAuth

class UserAuth {

    private val auth = FirebaseAuth.getInstance()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro ao fazer login") }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: (uid: String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) onSuccess(uid)
                else onError("Não foi possível obter o UID")
            }
            .addOnFailureListener { onError(it.message ?: "Erro ao criar conta") }
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Erro") }
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() = auth.signOut()
}