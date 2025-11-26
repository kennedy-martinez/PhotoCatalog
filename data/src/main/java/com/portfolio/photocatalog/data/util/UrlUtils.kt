package com.portfolio.photocatalog.data.util

private const val PLACEHOLDER_HOST = "placehold.co"
private const val EXTENSION_PNG = ".png"
private const val QUERY_PARAM_TEXT = "?text="
private const val REPLACEMENT_PNG_PARAM = ".png?text="

fun String?.sanitizeImageUrl(): String {
    if (this.isNullOrBlank()) return ""

    return if (this.contains(PLACEHOLDER_HOST) && !this.contains(EXTENSION_PNG)) {
        this.replace(QUERY_PARAM_TEXT, REPLACEMENT_PNG_PARAM)
    } else {
        this
    }
}