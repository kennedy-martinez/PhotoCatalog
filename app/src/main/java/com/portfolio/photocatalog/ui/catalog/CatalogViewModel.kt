package com.portfolio.photocatalog.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.portfolio.photocatalog.data.network.NetworkMonitor
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    getPhotoStreamUseCase: GetPhotoStreamUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val photoPagingFlow = getPhotoStreamUseCase()
        .cachedIn(viewModelScope)

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map { !it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun onToggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}