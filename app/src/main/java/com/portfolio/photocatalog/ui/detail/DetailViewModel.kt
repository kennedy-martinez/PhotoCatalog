package com.portfolio.photocatalog.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.photocatalog.domain.usecase.GetPhotoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPhotoDetailUseCase: GetPhotoDetailUseCase
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    val photoState = getPhotoDetailUseCase(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = null
        )

    private companion object {
        const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }
}