package com.portfolio.photocatalog.domain.usecase

import androidx.paging.PagingData
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotoStreamUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    operator fun invoke(): Flow<PagingData<PhotoItem>> {
        return repository.getPhotoStream()
    }
}