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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.portfolio.photocatalog.R
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoScreen(
    onPhotoClick: (String) -> Unit
) {
    val viewModel: CatalogViewModel = hiltViewModel()
    val photos = viewModel.photoPagingFlow.collectAsLazyPagingItems()
    val bannerState by viewModel.bannerState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullToRefreshState()
    val isRefreshing = photos.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = pullRefreshState,
            onRefresh = { photos.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                if (photos.itemCount > 0) {
                    PhotoList(
                        photos = photos,
                        onPhotoClick = onPhotoClick,
                        onToggleFavorite = viewModel::onToggleFavorite
                    )
                }

                if (photos.loadState.refresh is LoadState.Loading && photos.itemCount == 0) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                if (photos.loadState.refresh is LoadState.Error && photos.itemCount == 0) {
                    val error = (photos.loadState.refresh as LoadState.Error).error
                    ErrorMessage(
                        message = "Error loading data: ${error.localizedMessage ?: "Unknown error"}",
                        onRetry = { photos.retry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                when (val state = bannerState) {
                    is BannerUiState.Hidden -> {}
                    is BannerUiState.Offline -> {
                        StatusBanner(
                            message = stringResource(
                                R.string.status_offline_format,
                                state.lastUpdateDate
                            ),
                            color = MaterialTheme.colorScheme.error,
                            showButton = true,
                            onButtonClick = viewModel::forceSync,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    is BannerUiState.Online -> {
                        StatusBanner(
                            message = stringResource(
                                R.string.status_online_format,
                                state.lastUpdateMin,
                                state.nextUpdateMin
                            ),
                            color = Color(0xFFFFA000),
                            showButton = true,
                            onButtonClick = viewModel::forceSync,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    is BannerUiState.Updated -> {
                        StatusBanner(
                            message = stringResource(R.string.status_updated_just_now),
                            color = Color(0xFF4CAF50),
                            showButton = false,
                            onButtonClick = {},
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun StatusBanner(
    message: String,
    color: Color,
    showButton: Boolean,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
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
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )

            if (showButton) {
                IconButton(
                    onClick = onButtonClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.cd_force_sync),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoList(
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
fun PhotoItemCard(
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

private val DUMMY_PHOTO = PhotoItem(
    id = "101",
    description = "Mountain Landscape",
    imageUrl = "",
    confidence = 0.98f,
    isFavorite = false
)

@Preview(showBackground = true, name = "Error State")
@Composable
fun ErrorStatePreview() {
    PhotoCatalogTheme {
        ErrorMessage(
            message = "HTTP 401 Unauthorized",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Card Item")
@Composable
fun PhotoItemCardPreview() {
    PhotoCatalogTheme {
        PhotoItemCard(item = DUMMY_PHOTO, onPhotoClick = {}, onToggleFavorite = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "3. Full List with Banner")
@Composable
fun PhotoListPreview() {
    val fakePhotos = listOf(
        DUMMY_PHOTO,
        DUMMY_PHOTO.copy(id = "103", description = "Urban Street"),
        DUMMY_PHOTO.copy(id = "104", description = "Abstract Art")
    )

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
                    contentPadding = PaddingValues(
                        bottom = 80.dp,
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
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

                StatusBanner(
                    message = "Last update: 5 min ago. Next auto-sync in: 55 min",
                    color = Color(0xFFFFA000),
                    showButton = true,
                    onButtonClick = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}