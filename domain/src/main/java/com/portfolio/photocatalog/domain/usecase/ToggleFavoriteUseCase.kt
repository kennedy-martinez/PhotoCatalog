package com.portfolio.photocatalog.domain.usecase

import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.repository.CatalogRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(photo: PhotoItem) {
        repository.toggleFavorite(photo.id, !photo.isFavorite)
    }
}