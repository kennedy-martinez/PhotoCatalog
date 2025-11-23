package com.portfolio.photocatalog.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.portfolio.photocatalog.data.local.AppDatabase
import com.portfolio.photocatalog.data.local.entity.PhotoEntity
import com.portfolio.photocatalog.data.local.entity.RemoteKeys
import com.portfolio.photocatalog.data.local.entity.toEntity
import com.portfolio.photocatalog.data.network.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val database: AppDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, PhotoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val response = apiService.getPhotos(maxId = loadKey)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    val favoriteIds = database.photoDao().getFavoriteIds().toSet()

                    database.remoteKeysDao().clearRemoteKeys()
                    database.photoDao().clearAll()

                    val prevKey = null
                    val nextKey = if (response.isEmpty()) null else response.last().id
                    val keys = response.map { photo ->
                        RemoteKeys(photoId = photo.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    database.remoteKeysDao().insertAll(keys)

                    val entities = response.map { dto ->
                        val entity = dto.toEntity()
                        if (favoriteIds.contains(entity.id)) {
                            entity.copy(isFavorite = true)
                        } else {
                            entity
                        }
                    }
                    database.photoDao().insertAll(entities)

                } else {
                    val prevKey = null
                    val nextKey = if (response.isEmpty()) null else response.last().id
                    val keys = response.map { photo ->
                        RemoteKeys(photoId = photo.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    val favoriteIds = database.photoDao().getFavoriteIds().toSet()
                    val entities = response.map { dto ->
                        val entity = dto.toEntity()
                        if (favoriteIds.contains(entity.id)) entity.copy(isFavorite = true) else entity
                    }

                    database.remoteKeysDao().insertAll(keys)
                    database.photoDao().insertAll(entities)
                }
            }

            MediatorResult.Success(endOfPaginationReached = response.isEmpty())

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                database.remoteKeysDao().remoteKeysPhotoId(photo.id)
            }
    }
}