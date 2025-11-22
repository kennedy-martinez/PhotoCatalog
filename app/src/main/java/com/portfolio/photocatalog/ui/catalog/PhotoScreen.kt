package com.portfolio.photocatalog.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.portfolio.photocatalog.R
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(
    onPhotoClick: (String) -> Unit
) {
    val viewModel: CatalogViewModel = hiltViewModel()
    val photos = viewModel.photoPagingFlow.collectAsLazyPagingItems()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PhotoList(
                photos = photos,
                onPhotoClick = onPhotoClick,
                onToggleFavorite = viewModel::onToggleFavorite
            )

            if (photos.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (isOffline) {
                OfflineBanner(
                    onRefreshClick = { photos.refresh() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun PhotoList(
    photos: LazyPagingItems<PhotoItem>,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (PhotoItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(count = photos.itemCount) { index ->
            val item = photos[index]
            if (item != null) {
                PhotoItemCard(
                    item = item,
                    onPhotoClick = onPhotoClick,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }

        if (photos.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun PhotoItemCard(
    item: PhotoItem,
    onPhotoClick: (String) -> Unit,
    onToggleFavorite: (PhotoItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPhotoClick(item.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = stringResource(R.string.cd_photo_image),
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.label_confidence, item.confidence),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.label_id, item.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = { onToggleFavorite(item) }) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (item.isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun OfflineBanner(
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.status_offline_mode),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.cd_force_sync),
                    tint = Color.White
                )
            }
        }
    }
}

private val DUMMY_PHOTO = PhotoItem(
    id = "101",
    description = "Mountain Landscape",
    imageUrl = "",
    confidence = 0.98f,
    isFavorite = false
)

private val DUMMY_PHOTO_FAV = PhotoItem(
    id = "102",
    description = "Cute Cat (Favorite)",
    imageUrl = "",
    confidence = 0.99f,
    isFavorite = true
)

@Preview(showBackground = true, name = "1. Card Item (Normal)")
@Composable
fun PhotoItemCardPreview() {
    PhotoCatalogTheme {
        PhotoItemCard(item = DUMMY_PHOTO, onPhotoClick = {}, onToggleFavorite = {})
    }
}

@Preview(showBackground = true, name = "2. Card Item (Favorite)")
@Composable
fun PhotoItemCardFavPreview() {
    PhotoCatalogTheme {
        PhotoItemCard(item = DUMMY_PHOTO_FAV, onPhotoClick = {}, onToggleFavorite = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "3. Full List with Offline Banner")
@Composable
fun PhotoListPreview() {
    val fakePhotos = listOf(
        DUMMY_PHOTO,
        DUMMY_PHOTO_FAV,
        DUMMY_PHOTO.copy(id = "103", description = "Urban Street Photography"),
        DUMMY_PHOTO.copy(id = "104", description = "Abstract Art", confidence = 0.5f),
        DUMMY_PHOTO_FAV.copy(id = "105", description = "Family Portrait"),
        DUMMY_PHOTO.copy(id = "106", description = "Another Photo")
    )

    val flow = flowOf(PagingData.from(fakePhotos))
    val lazyPagingItems = flow.collectAsLazyPagingItems()

    PhotoCatalogTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(fakePhotos.size) { index ->
                        PhotoItemCard(
                            item = fakePhotos[index],
                            onPhotoClick = {},
                            onToggleFavorite = {}
                        )
                    }
                }

                OfflineBanner(
                    onRefreshClick = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}