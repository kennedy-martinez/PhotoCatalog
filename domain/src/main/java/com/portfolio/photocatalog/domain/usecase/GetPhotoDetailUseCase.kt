package com.portfolio.photocatalog.domain.usecase

import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    operator fun invoke(photoId: String): Flow<PhotoItem?> {
        return repository.getPhotoDetail(photoId)
    }
}