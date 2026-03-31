package com.victor.postly.model

data class Post(
    val description: String = "",
    val image: String? = null,
    val timestamp: Long = 0L
)