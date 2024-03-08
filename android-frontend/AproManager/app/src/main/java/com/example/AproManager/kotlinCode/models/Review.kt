package com.example.AproManager.kotlinCode.models


data class Review(
    var reviewId: String = "",
    var rating: Float = 0f,
    var review: String = "",
    var reviewBy: String = "",
    var reviewTime: Long = 0,
    var profileUri: String = ""
) {
    // Default constructor
    constructor() : this("", 0f, "", "", 0, "")
}



