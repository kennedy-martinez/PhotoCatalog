package com.portfolio.photocatalog

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.portfolio.photocatalog.data.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CatalogApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve(DISK_CACHE_NAME))
                    .maxSizeBytes(DISK_CACHE_SIZE_BYTES)
                    .build()
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .respectCacheHeaders(true)
            .build()
    }

    private companion object {
        const val MEMORY_CACHE_PERCENT = 0.25
        const val DISK_CACHE_NAME = "image_cache"
        const val DISK_CACHE_SIZE_BYTES = 100L * 1024 * 1024
    }
}