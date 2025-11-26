package com.portfolio.photocatalog.data

import com.portfolio.photocatalog.data.util.sanitizeImageUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class UrlUtilsTest {

    @Test
    fun `sanitizeImageUrl fixes placehold-co svg urls`() {
        val badUrl = "https://placehold.co/512x512?text=Hello"
        val result = badUrl.sanitizeImageUrl()
        val expected = "https://placehold.co/512x512.png?text=Hello"
        assertEquals(expected, result)
    }

    @Test
    fun `sanitizeImageUrl does nothing to correct png urls`() {
        val goodUrl = "https://placehold.co/512x512.png?text=Good"
        val result = goodUrl.sanitizeImageUrl()
        assertEquals(goodUrl, result)
    }

    @Test
    fun `sanitizeImageUrl ignores other domains`() {
        val otherUrl = "https://google.com/image.jpg"
        val result = otherUrl.sanitizeImageUrl()
        assertEquals(otherUrl, result)
    }

    @Test
    fun `sanitizeImageUrl handles null or empty`() {
        assertEquals("", null.sanitizeImageUrl())
        assertEquals("", "".sanitizeImageUrl())
    }
}