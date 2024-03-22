package com.example.AproManager.kotlinCode.models

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>
)
