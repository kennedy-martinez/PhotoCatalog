package com.portfolio.photocatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.portfolio.photocatalog.ui.catalog.PhotoScreen
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoCatalogTheme {
                PhotoScreen()
            }
        }
    }
}