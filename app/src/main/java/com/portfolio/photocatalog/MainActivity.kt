package com.portfolio.photocatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.portfolio.photocatalog.ui.catalog.PhotoScreen
import com.portfolio.photocatalog.ui.detail.DetailScreen
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoCatalogTheme {
                PhotoAppNavigation()
            }
        }
    }
}

@Composable
fun PhotoAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "catalog") {

        composable("catalog") {
            PhotoScreen(
                onPhotoClick = { photoId ->
                    navController.navigate("detail/$photoId")
                }
            )
        }

        composable(
            route = "detail/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}