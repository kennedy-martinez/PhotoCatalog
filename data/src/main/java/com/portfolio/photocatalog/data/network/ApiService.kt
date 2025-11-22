package com.portfolio.photocatalog.data.network

import com.portfolio.photocatalog.data.network.model.PhotoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("items")
    suspend fun getPhotos(
        @Query("since_id") sinceId: String? = null,
        @Query("max_id") maxId: String? = null
    ): List<PhotoDto>
}