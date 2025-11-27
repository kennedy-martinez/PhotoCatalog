package com.portfolio.photocatalog.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.portfolio.photocatalog.data.local.AppDatabase
import com.portfolio.photocatalog.data.local.PreferenceStorage
import com.portfolio.photocatalog.data.local.entity.toDomain
import com.portfolio.photocatalog.data.local.entity.toEntity
import com.portfolio.photocatalog.data.network.ApiService
import com.portfolio.photocatalog.data.network.model.PhotoDto
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.repository.CatalogRepository
import com.portfolio.photocatalog.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase,
    private val preferenceStorage: PreferenceStorage
) : CatalogRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPhotoStream(): Flow<PagingData<PhotoItem>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
            remoteMediator = PhotoRemoteMediator(database, apiService, preferenceStorage),
            pagingSourceFactory = { database.photoDao().getPhotos() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getPhotoDetail(photoId: String): Flow<PhotoItem?> {
        return database.photoDao().getPhotoById(photoId).map { it?.toDomain() }
    }

    override suspend fun refreshAllContent(): Result<Unit> {
        return try {
            var currentMaxId: String? = null
            var hasMoreData = true
            val allPhotos = mutableListOf<PhotoDto>()

            while (hasMoreData) {
                val response = apiService.getPhotos(maxId = currentMaxId)
                if (response.isNotEmpty()) {
                    allPhotos.addAll(response)
                    currentMaxId = response.last().id
                    if (response.size < 10) hasMoreData = false
                } else {
                    hasMoreData = false
                }
            }

            if (allPhotos.isNotEmpty()) {
                database.withTransaction {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.photoDao().clearAll()
                    database.photoDao().insertAll(allPhotos.map { it.toEntity() })
                }
                preferenceStorage.updateLastSyncTime(System.currentTimeMillis())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    companion object {
        private const val ITEMS_PER_PAGE = 10
    }
}