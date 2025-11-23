package com.portfolio.photocatalog.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.portfolio.photocatalog.data.local.PreferenceStorage
import com.portfolio.photocatalog.data.network.NetworkMonitor
import com.portfolio.photocatalog.data.worker.SyncWorker
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    getPhotoStreamUseCase: GetPhotoStreamUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    networkMonitor: NetworkMonitor,
    private val preferenceStorage: PreferenceStorage,
    private val workManager: WorkManager
) : ViewModel() {

    val photoPagingFlow = getPhotoStreamUseCase()
        .cachedIn(viewModelScope)

    val bannerState = combine(
        networkMonitor.isOnline,
        preferenceStorage.lastSyncTimestamp
    ) { isOnline, lastSync ->
        createBannerState(isOnline, lastSync)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BannerUiState.Hidden
    )

    fun onToggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }

    fun forceSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueueUniqueWork("force_sync_user", ExistingWorkPolicy.KEEP, request)
    }

    private fun createBannerState(isOnline: Boolean, lastSync: Long): BannerUiState {
        if (lastSync == 0L) return BannerUiState.Hidden

        val now = System.currentTimeMillis()
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(now - lastSync)
        val nextUpdateMinutes = 60 - diffMinutes

        return if (!isOnline) {
            val dateString =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(lastSync))
            BannerUiState.Offline(dateString)
        } else {
            if (diffMinutes < 1) {
                BannerUiState.Updated
            } else {
                BannerUiState.Online(
                    lastUpdateMin = diffMinutes,
                    nextUpdateMin = maxOf(0, nextUpdateMinutes)
                )
            }
        }
    }
}

sealed class BannerUiState {
    data object Hidden : BannerUiState()
    data object Updated : BannerUiState()
    data class Offline(val lastUpdateDate: String) : BannerUiState()
    data class Online(val lastUpdateMin: Long, val nextUpdateMin: Long) : BannerUiState()
}