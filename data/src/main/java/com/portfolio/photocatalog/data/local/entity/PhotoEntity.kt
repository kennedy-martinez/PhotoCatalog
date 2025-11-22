package com.portfolio.photocatalog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.data.network.model.PhotoDto

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val text: String,
    val url: String,
    val confidence: Float,
    val isFavorite: Boolean = false
)

fun PhotoEntity.toDomain(): PhotoItem {
    return PhotoItem(
        id = id,
        description = text,
        imageUrl = url,
        confidence = confidence,
        isFavorite = isFavorite
    )
}

fun PhotoDto.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        text = text ?: "",
        url = url ?: "",
        confidence = confidence ?: 0.0f,
        isFavorite = false
    )
}