package com.example.AproManager.kotlinCode.retrofit;

// ImageRequest.kt
data class ImageRequest(
        val inputs: String
)

// ImageResponse.kt
data class ImageResponse(
        val imageUrl: String? = null,
        val status: String? = null,
        val errorMessage: String? = null
)
