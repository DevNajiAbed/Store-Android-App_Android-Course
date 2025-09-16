package com.pcit.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.pcit.shop.db.ProductDatabase
import com.pcit.shop.screen.AddEditProductScreen
import com.pcit.shop.screen.ProductListScreen
import com.pcit.shop.ui.theme.ShopTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = Room.databaseBuilder(
                context = this,
                ProductDatabase::class.java,
                name = "ProductDatabase"
            ).build()

            ShopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.ProductListScreen
                ) {
                    composable<Route.ProductListScreen> {
                        ProductListScreen(
                            onProductClick = {
                                navController.navigate(
                                    Route.AddEditProductScreen(it)
                                )
                            },
                            onAddProductClick = {
                                navController.navigate(
                                    Route.AddEditProductScreen(null)
                                )
                            },
                            db = db
                        )
                    }
                    composable<Route.AddEditProductScreen> {
                        val id = it.toRoute<Route.AddEditProductScreen>().id
                        AddEditProductScreen(
                            productId = id,
                            db = db,
                            navigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}

private sealed interface Route {
    @Serializable
    data object ProductListScreen : Route

    @Serializable
    data class AddEditProductScreen(
        val id: Int?
    ) : Route
}