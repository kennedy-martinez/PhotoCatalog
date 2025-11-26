package com.portfolio.photocatalog.domain.usecase

import com.portfolio.photocatalog.domain.model.SyncStatus
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GetSyncStatusUseCase @Inject constructor() {

    operator fun invoke(isOnline: Boolean, lastSyncTime: Long): SyncStatus {
        if (lastSyncTime == 0L) return SyncStatus.NeverSynced

        if (!isOnline) return SyncStatus.Offline(lastSyncTime)

        val now = System.currentTimeMillis()
        val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(now - lastSyncTime)

        return if (diffMinutes < FRESH_DATA_THRESHOLD_MINUTES) {
            SyncStatus.DataIsFresh
        } else {
            SyncStatus.DataIsStale(lastSyncTime)
        }
    }

    private companion object {
        const val FRESH_DATA_THRESHOLD_MINUTES = 1
    }
}