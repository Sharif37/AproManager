package com.example.AproManager.kotlinCode.models


data class Review(
    var reviewId: String = "",
    var rating: Float = 0f,
    var review: String = "",
    var reviewBy: String = "",
    var reviewTime: Long = 0L,
    var profileUri: String = ""
) {
    // Default constructor
    constructor() : this("", 0f, "", "", 0, "")

    // Constructor with Int type for reviewTime
    constructor(
        rating: Float,
        review: String,
        reviewBy: String,
        reviewTime: Long,
        profileUri: String
    ) : this("", rating, review, reviewBy, reviewTime, profileUri)


}




