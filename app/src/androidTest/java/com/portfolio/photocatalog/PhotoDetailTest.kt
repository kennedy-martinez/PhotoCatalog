package com.portfolio.photocatalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.portfolio.photocatalog.domain.model.PhotoItem
import com.portfolio.photocatalog.ui.detail.PhotoDetailContent
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PhotoDetailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testItem = PhotoItem(
        id = "999",
        description = "Test Detail Description",
        imageUrl = "https://example.com/image.png",
        confidence = 0.123f
    )

    @Test
    fun detailContent_displaysCorrectInfo() {
        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoDetailContent(
                    item = testItem,
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Detail Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("999", substring = true).assertIsDisplayed()
    }

    @Test
    fun backButton_triggersCallback() {
        var backClicked = false

        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoDetailContent(
                    item = testItem,
                    onBackClick = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backClicked)
    }

    @Test
    fun zoomDialog_opensAndCloses() {
        composeTestRule.setContent {
            PhotoCatalogTheme {
                PhotoDetailContent(
                    item = testItem,
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Close").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Photo content").performClick()
        composeTestRule.onNodeWithContentDescription("Close").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Close").performClick()
        composeTestRule.onNodeWithContentDescription("Close").assertDoesNotExist()
    }
}