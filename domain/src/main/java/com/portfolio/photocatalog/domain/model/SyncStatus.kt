package com.portfolio.photocatalog.domain.model

sealed class SyncStatus {
    data object NeverSynced : SyncStatus()
    data class Offline(val lastSyncTime: Long) : SyncStatus()
    data object DataIsFresh : SyncStatus()
    data class DataIsStale(val lastSyncTime: Long) : SyncStatus()
}