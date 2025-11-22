package com.portfolio.photocatalog.domain.model

data class PhotoItem(
    val id: String,
    val description: String,
    val imageUrl: String,
    val confidence: Float
)