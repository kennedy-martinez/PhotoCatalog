package com.portfolio.photocatalog.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.usecase.GetPhotoDetailUseCase
import com.portfolio.photocatalog.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    val photoState = getPhotoDetailUseCase(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = null
        )

    fun onToggleFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }
}