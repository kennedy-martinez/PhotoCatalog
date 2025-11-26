package com.portfolio.photocatalog.data.network.model

import com.portfolio.photocatalog.data.util.sanitizeImageUrl
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDto(
    @Json(name = "_id") val id: String,
    @Json(name = "text") val text: String,
    @Json(name = "image") val url: String,
    @Json(name = "confidence") val confidence: Float
)

fun PhotoDto.toDomain(): PhotoItem {

    return PhotoItem(
        id = id,
        description = text,
        imageUrl = url.sanitizeImageUrl(),
        confidence = confidence
    )
}