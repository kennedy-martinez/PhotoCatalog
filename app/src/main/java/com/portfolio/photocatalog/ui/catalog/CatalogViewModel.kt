package com.portfolio.photocatalog.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    getPhotoStreamUseCase: GetPhotoStreamUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val photoPagingFlow = getPhotoStreamUseCase()
        .cachedIn(viewModelScope)

    fun onToggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}