package com.portfolio.photocatalog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.ui.catalog.ErrorMessage
import com.portfolio.photocatalog.ui.catalog.PhotoList
import com.portfolio.photocatalog.ui.catalog.StatusBanner
import com.portfolio.photocatalog.ui.detail.PhotoDetailContent
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PhotoComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
                    onPhotoClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Item One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item Two").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item Three").assertIsDisplayed()
    }

    @Test
    fun photoList_navigates_whenItemClicked() {
        var clickedId: String? = null
        val testItem = PhotoItem("999", "Click Me", "", 0.9f)
        val testList = listOf(testItem)

        composeTestRule.setContent {
            val flow = flowOf(PagingData.from(testList))
            val lazyPagingItems = flow.collectAsLazyPagingItems()

            PhotoCatalogTheme {
                PhotoList(
                    photos = lazyPagingItems,
                    onPhotoClick = { clickedId = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Click Me").performClick()

        assertEquals("999", clickedId)
    }

    @Test
    fun detailContent_displaysAllMetadata() {
        val detailItem = PhotoItem(
            id = "555",
            description = "Super Detailed Description",
            imageUrl = "",
            confidence = 0.1234f
        )

        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoDetailContent(
                    item = detailItem,
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Super Detailed Description").assertIsDisplayed()

        composeTestRule.onNodeWithText("ID: 555").assertIsDisplayed()
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