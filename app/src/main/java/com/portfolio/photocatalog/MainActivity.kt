package com.portfolio.photocatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.portfolio.photocatalog.ui.catalog.PhotoScreen
import com.portfolio.photocatalog.ui.detail.DetailScreen
import com.portfolio.photocatalog.ui.theme.PhotoCatalogTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            delay(1500)
            keepSplash = false
        }
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