package com.portfolio.photocatalog.domain

import com.portfolio.photocatalog.domain.model.SyncStatus
import com.portfolio.photocatalog.domain.usecase.GetSyncStatusUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSyncStatusUseCaseTest {

    private val useCase = GetSyncStatusUseCase()

    @Test
    fun `invoke returns NeverSynced when lastSyncTime is 0`() {
        val isOnline = true
        val lastSync = 0L
        val result = useCase(isOnline, lastSync)
        assertEquals(SyncStatus.NeverSynced, result)
    }

    @Test
    fun `invoke returns Offline when isOnline is false`() {
        val isOnline = false
        val lastSync = 123456789L
        val result = useCase(isOnline, lastSync)
        assertTrue(result is SyncStatus.Offline)
        assertEquals(lastSync, (result as SyncStatus.Offline).lastSyncTime)
    }

    @Test
    fun `invoke returns DataIsFresh when sync was less than 1 minute ago`() {
        val isOnline = true
        val now = System.currentTimeMillis()
        val lastSync = now - 30_000
        val result = useCase(isOnline, lastSync)
        assertEquals(SyncStatus.DataIsFresh, result)
    }

    @Test
    fun `invoke returns DataIsStale when sync was more than 1 minute ago`() {
        val isOnline = true
        val now = System.currentTimeMillis()
        val lastSync = now - 65_000
        val result = useCase(isOnline, lastSync)
        assertTrue(result is SyncStatus.DataIsStale)
    }
}