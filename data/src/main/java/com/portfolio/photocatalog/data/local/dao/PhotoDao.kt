package com.portfolio.photocatalog.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.portfolio.photocatalog.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos")
    fun getPhotos(): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoById(photoId: String): Flow<PhotoEntity?>

    @Query("DELETE FROM photos")
    suspend fun clearAll()

    @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :photoId")
    suspend fun updateFavoriteStatus(photoId: String, isFavorite: Boolean)

    @Query("SELECT id FROM photos WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>
}