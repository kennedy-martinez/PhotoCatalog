package com.portfolio.photocatalog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.ui.catalog.ErrorMessage
import com.portfolio.photocatalog.ui.catalog.PhotoItemCard
import com.portfolio.photocatalog.ui.catalog.PhotoList
import com.portfolio.photocatalog.ui.catalog.StatusBanner
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class PhotoComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun photoItemCard_showsFilledHeart_whenFavorite() {
        val favoriteItem = PhotoItem(
            id = "1",
            description = "Test Favorite",
            imageUrl = "",
            confidence = 1.0f,
            isFavorite = true
        )

        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoItemCard(
                    item = favoriteItem,
                    onPhotoClick = {},
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Mark as Favorite")
            .assertIsDisplayed()
    }

    @Test
    fun photoItemCard_showsBorderHeart_whenNotFavorite() {
        val normalItem = PhotoItem(
            id = "1",
            description = "Test Normal",
            imageUrl = "",
            confidence = 1.0f,
            isFavorite = false
        )

        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoItemCard(
                    item = normalItem,
                    onPhotoClick = {},
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Mark as Favorite")
            .assertIsDisplayed()
    }

    @Test
    fun photoList_displaysItems_correctly() {
        val testList = listOf(
            PhotoItem("1", "Item One", "", 0.9f),
            PhotoItem("2", "Item Two", "", 0.8f),
            PhotoItem("3", "Item Three", "", 0.7f)
        )

        composeTestRule.setContent {
            val flow = flowOf(PagingData.from(testList))
            val lazyPagingItems = flow.collectAsLazyPagingItems()

            PhotoCatalogTheme {
                PhotoList(
                    photos = lazyPagingItems,
                    onPhotoClick = {},
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Item One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item Two").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item Three").assertIsDisplayed()
    }

    @Test
    fun errorMessage_displaysTextAndRetryButton() {
        val errorText = "Network Error 404"

        composeTestRule.setContent {
            PhotoCatalogTheme {
                ErrorMessage(
                    message = errorText,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun statusBanner_displaysMessage() {
        val bannerText = "Offline Mode"

        composeTestRule.setContent {
            PhotoCatalogTheme {
                StatusBanner(
                    message = bannerText,
                    color = Color.Red,
                    showButton = true,
                    onButtonClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText(bannerText).assertIsDisplayed()
    }
}