package com.portfolio.photocatalog.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.portfolio.photocatalog.data.local.AppDatabase
import com.portfolio.photocatalog.data.local.entity.toDomain
import com.portfolio.photocatalog.data.network.ApiService
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) : CatalogRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPhotoStream(): Flow<PagingData<PhotoItem>> {
        val pagingSourceFactory = { database.photoDao().getPhotos() }

        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            remoteMediator = PhotoRemoteMediator(
                database = database,
                apiService = apiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getPhotoDetail(photoId: String): Flow<PhotoItem?> {
        return database.photoDao().getPhotoById(photoId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun toggleFavorite(photoId: String, isFavorite: Boolean) {
        database.photoDao().updateFavoriteStatus(photoId, isFavorite)
    }

    companion object {
        private const val ITEMS_PER_PAGE = 10
    }
}