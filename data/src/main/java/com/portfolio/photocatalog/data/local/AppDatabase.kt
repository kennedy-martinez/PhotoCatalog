package com.portfolio.photocatalog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.portfolio.photocatalog.data.local.dao.PhotoDao
import com.portfolio.photocatalog.data.local.dao.RemoteKeysDao
import com.portfolio.photocatalog.data.local.entity.PhotoEntity
import com.portfolio.photocatalog.data.local.entity.RemoteKeys

@Database(
    entities = [PhotoEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "photocatalog_db"
    }
}