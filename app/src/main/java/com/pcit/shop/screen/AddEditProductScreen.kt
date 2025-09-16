package com.pcit.shop.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.room.InvalidationTracker
import coil3.compose.AsyncImage
import com.pcit.shop.R
import com.pcit.shop.db.ProductDao
import com.pcit.shop.db.ProductDatabase
import com.pcit.shop.model.Product
import com.pcit.shop.ui.theme.ShopTheme
import kotlinx.coroutines.launch

@Composable
fun AddEditProductScreen(
    modifier: Modifier = Modifier,
    productId: Int? = null,
    db: ProductDatabase,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let { uri ->
            imageUri = uri
        }
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    productId?.let { id ->
        scope.launch {
            val product = db.productDao.getProductById(id)
            imageUri = product.image.toUri()
            title = product.title
            description = product.description
            price = product.price.toString()
        }
    }

    val descriptionFocusRequester = remember { FocusRequester() }
    val priceFocusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUri ?: R.drawable.ic_launcher_background,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(24.dp),
                            color = Color.LightGray
                        )
                        .clickable {
                            launcher.launch(
                                PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        }
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Title..."
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            descriptionFocusRequester.requestFocus()
                        }
                    )
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descriptionFocusRequester),
                    placeholder = {
                        Text(
                            text = "Description..."
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            priceFocusRequester.requestFocus()
                        }
                    )
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(priceFocusRequester),
                    placeholder = {
                        Text(
                            text = "Price..."
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
            Button(
                onClick = {
                    if (imageUri == null) {
                        Toast.makeText(context, "Pick a photo for the product", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (title.trim().isBlank()) {
                        Toast.makeText(context, "Enter a title for the product", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (description.trim().isBlank()) {
                        Toast.makeText(
                            context,
                            "Enter a description for the product",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (price.toFloat() == 0f) {
                        Toast.makeText(
                            context,
                            "Insert the price of the product",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    scope.launch {
                        db.productDao
                            .upsertProduct(
                                Product(
                                    id = productId ?: 0,
                                    title = title,
                                    description = description,
                                    price = price.toFloat(),
                                    image = imageUri.toString()
                                )
                            )

                        navigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = productId?.let { "Save" } ?: "Add Product",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewAddEditProductScreen() {
    ShopTheme {
        AddEditProductScreen(
            db = object : ProductDatabase() {
                override val productDao: ProductDao
                    get() = TODO("Not yet implemented")

                override fun clearAllTables() {
                    TODO("Not yet implemented")
                }

                override fun createInvalidationTracker(): InvalidationTracker {
                    TODO("Not yet implemented")
                }
            },
            navigateBack = {}
        )
    }
}