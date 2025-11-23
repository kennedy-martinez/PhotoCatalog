package com.portfolio.photocatalog.domain.repository

import androidx.paging.PagingData
import com.portfolio.photocatalog.domain.model.PhotoItem
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {

    fun getPhotoStream(): Flow<PagingData<PhotoItem>>

    fun getPhotoDetail(photoId: String): Flow<PhotoItem?>

    suspend fun toggleFavorite(photoId: String, isFavorite: Boolean)

    suspend fun refreshAllContent(): Result<Unit>
}