package com.victor.postly.model

import android.graphics.Bitmap

data class Post(
    val description: String = "",
    val image: Bitmap? = null
)