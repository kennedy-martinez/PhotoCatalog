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
import com.portfolio.photocatalog.domain.model.SyncStatus
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import com.portfolio.photocatalog.domain.usecase.GetSyncStatusUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    getPhotoStreamUseCase: GetPhotoStreamUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getSyncStatusUseCase: GetSyncStatusUseCase,
    networkMonitor: NetworkMonitor,
    private val preferenceStorage: PreferenceStorage,
    private val workManager: WorkManager
) : ViewModel() {

    val photoPagingFlow = getPhotoStreamUseCase()
        .cachedIn(viewModelScope)

    private val _isVisualSyncing = kotlinx.coroutines.flow.MutableStateFlow(false)

    val bannerState = combine(
        networkMonitor.isOnline,
        preferenceStorage.lastSyncTimestamp,
        tickerFlow(TICKER_UPDATE_INTERVAL_MS),
        _isVisualSyncing
    ) { isOnline, lastSync, _, isVisualSyncing ->
        if (isVisualSyncing) {
            BannerUiState.Syncing
        } else {
            val status = getSyncStatusUseCase(isOnline, lastSync)
            mapToUiState(status)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = BannerUiState.Hidden
    )

    fun onToggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }

    fun forceSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, request)
    }

    fun triggerVisualSync() {
        viewModelScope.launch {
            _isVisualSyncing.value = true
            forceSync()
            delay(MIN_LOADING_TIME_MS)
            _isVisualSyncing.value = false
        }
    }

    private fun mapToUiState(status: SyncStatus): BannerUiState {
        return when (status) {
            is SyncStatus.NeverSynced -> BannerUiState.Hidden

            is SyncStatus.Offline -> BannerUiState.Offline(status.lastSyncTime)

            is SyncStatus.DataIsFresh -> BannerUiState.Updated

            is SyncStatus.DataIsStale -> {
                val now = System.currentTimeMillis()
                val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(now - status.lastSyncTime)
                val nextUpdateMinutes = SYNC_INTERVAL_MINUTES - diffMinutes

                BannerUiState.Online(
                    lastUpdateMin = diffMinutes,
                    nextUpdateMin = maxOf(0, nextUpdateMinutes)
                )
            }
        }
    }

    private fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private companion object {
        const val TICKER_UPDATE_INTERVAL_MS = 60_000L
        const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
        const val MIN_LOADING_TIME_MS = 2_000L
        const val UNIQUE_WORK_NAME = "force_sync_user"
        const val SYNC_INTERVAL_MINUTES = 60
    }
}

sealed class BannerUiState {
    data object Hidden : BannerUiState()
    data object Syncing : BannerUiState()
    data object Updated : BannerUiState()
    data class Offline(val lastSyncTime: Long) : BannerUiState()
    data class Online(val lastUpdateMin: Long, val nextUpdateMin: Long) : BannerUiState()
}