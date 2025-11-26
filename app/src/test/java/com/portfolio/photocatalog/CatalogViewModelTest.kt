package com.portfolio.photocatalog

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import app.cash.turbine.test
import com.portfolio.photocatalog.data.local.PreferenceStorage
import com.portfolio.photocatalog.data.network.NetworkMonitor
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.model.SyncStatus
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import com.portfolio.photocatalog.domain.usecase.GetSyncStatusUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import com.portfolio.photocatalog.ui.catalog.BannerUiState
import com.portfolio.photocatalog.ui.catalog.CatalogViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val getPhotoStreamUseCase: GetPhotoStreamUseCase = mockk(relaxed = true)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)
    private val getSyncStatusUseCase: GetSyncStatusUseCase = mockk()
    private val networkMonitor: NetworkMonitor = mockk()
    private val preferenceStorage: PreferenceStorage = mockk()
    private val workManager: WorkManager = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CatalogViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { networkMonitor.isOnline } returns flowOf(true)
        every { preferenceStorage.lastSyncTimestamp } returns flowOf(1000L)
        every { getSyncStatusUseCase(any(), any()) } returns SyncStatus.DataIsFresh
        every { getPhotoStreamUseCase() } returns flowOf()
        every { workManager.getWorkInfosForUniqueWorkFlow(any()) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `bannerState emits Syncing when triggerVisualSync is called`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        viewModel.bannerState.test {
            assertEquals(BannerUiState.Hidden, awaitItem())

            val calculatedState = awaitItem()
            assertTrue(
                "Expected Updated but got $calculatedState",
                calculatedState is BannerUiState.Updated
            )

            viewModel.triggerVisualSync()

            val syncingState = awaitItem()
            assertTrue(syncingState is BannerUiState.Syncing)

            advanceTimeBy(2001)

            val finalState = awaitItem()
            assertTrue(finalState is BannerUiState.Updated)
        }
    }

    @Test
    fun `bannerState emits Offline when SyncStatus is Offline`() = runTest(testDispatcher) {
        every { getSyncStatusUseCase(any(), any()) } returns SyncStatus.Offline(123456L)

        viewModel = createViewModel()

        viewModel.bannerState.test {
            awaitItem()
            val item = awaitItem()
            assertTrue(item is BannerUiState.Offline)
        }
    }

    @Test
    fun `bannerState emits Online when SyncStatus is DataIsStale`() = runTest(testDispatcher) {
        every { getSyncStatusUseCase(any(), any()) } returns SyncStatus.DataIsStale(1000L)

        viewModel = createViewModel()

        viewModel.bannerState.test {
            awaitItem()

            val item = awaitItem()
            assertTrue(item is BannerUiState.Online)
        }
    }

    @Test
    fun `forceSync enqueues WorkManager task`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        viewModel.forceSync()

        verify {
            workManager.enqueueUniqueWork(
                "force_sync_user",
                ExistingWorkPolicy.KEEP,
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `onToggleFavorite calls UseCase`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        val photo = PhotoItem("1", "Test", "", 0f)

        viewModel.onToggleFavorite(photo)
        advanceTimeBy(100)

        coVerify { toggleFavoriteUseCase(photo) }
    }

    private fun createViewModel(): CatalogViewModel {
        return CatalogViewModel(
            getPhotoStreamUseCase,
            toggleFavoriteUseCase,
            getSyncStatusUseCase,
            networkMonitor,
            preferenceStorage,
            workManager
        )
    }
}