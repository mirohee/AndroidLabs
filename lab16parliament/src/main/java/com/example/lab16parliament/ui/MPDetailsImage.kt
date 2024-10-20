package com.example.lab16parliament.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.util.DebugLogger
import com.example.lab16parliament.R


/**
 * Miro Saarinen
 * 21/10/2024
 * Display the MP image using the AsyncImage composable.
 */
@Composable
fun MPDetailsImage(pictureUrl: String?) {
    // Get the context to create the ImageLoader
    val context = LocalContext.current

    // Create a custom ImageLoader with a DebugLogger
    val imageLoader = context.imageLoader.newBuilder()
        .logger(DebugLogger())
        .build()

    if (!pictureUrl.isNullOrEmpty()) {
        AsyncImage(
            model = "https://avoindata.eduskunta.fi/$pictureUrl",
            contentDescription = "MP Image",
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp),
            imageLoader = imageLoader
        )
    } else {
        AsyncImage(
            model = R.drawable.default_image,
            contentDescription = "Default MP Image",
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp)
        )
    }
}
