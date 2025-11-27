package com.portfolio.photocatalog.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.portfolio.photocatalog.data.local.entity.PhotoEntity

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getPhotos(): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhotoById(photoId: String): kotlinx.coroutines.flow.Flow<PhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearAll()
}