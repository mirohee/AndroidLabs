package com.example.lab08networking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab08networking.ui.theme.AndroidsensorlabsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidsensorlabsTheme {
                NetworkImageView("https://users.metropolia.fi/~jarkkov/folderimage.jpg")
            }
        }
    }
}

suspend fun downloadImage(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(url).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun NetworkImageView(url: String, modifier: Modifier = Modifier) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(url) {
        scope.launch {
            bitmap = downloadImage(url)
            isLoading = false
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = url,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        if (isLoading) {
            Text(
                text = "Loading Image...",
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (bitmap != null) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).fillMaxSize()
                )
            }
        } else {
            Text(
                text = "Failed to Load Image",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun PreviewNetworkImageView() {
    AndroidsensorlabsTheme {
        NetworkImageView("https://users.metropolia.fi/~jarkkov/folderimage.jpg")
    }
}
