package com.pcit.shop.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.InvalidationTracker
import coil3.compose.AsyncImage
import com.pcit.shop.db.ProductDao
import com.pcit.shop.db.ProductDatabase
import com.pcit.shop.model.Product
import com.pcit.shop.ui.theme.ShopTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    onAddProductClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    db: ProductDatabase
) {
    val products by db
        .productDao
        .getAllProducts()
        .collectAsStateWithLifecycle(emptyList())

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shop"
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add new product"
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductItem(
                        modifier = Modifier
                            .clickable {
                                onProductClick(product.id)
                            },
                        product = product
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = product.image.toUri(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "$${product.price}",
                color = Color.Blue,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            text = product.description,
            fontSize = 16.sp,
            color = Color.LightGray,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewProductListScreen() {
    ShopTheme {
        ProductListScreen(
            onAddProductClick = {},
            onProductClick = {},
            db = object : ProductDatabase() {
                override val productDao: ProductDao
                    get() = TODO("Not yet implemented")

                override fun clearAllTables() {
                    TODO("Not yet implemented")
                }

                override fun createInvalidationTracker(): InvalidationTracker {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProductItem() {
    ShopTheme {
        ProductItem(
            product = Product(
                0,
                "Product",
                "This is Product",
                "",
                12.99f
            )
        )
    }
}