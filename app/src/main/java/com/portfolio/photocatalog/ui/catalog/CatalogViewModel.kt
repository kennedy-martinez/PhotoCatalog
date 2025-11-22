package com.portfolio.photocatalog.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.portfolio.photocatalog.domain.usecase.GetPhotoStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    getPhotoStreamUseCase: GetPhotoStreamUseCase
) : ViewModel() {

    val photoPagingFlow = getPhotoStreamUseCase()
        .cachedIn(viewModelScope)
}