package `in`.surajsau.jisho.ui.home

import `in`.surajsau.jisho.base.use
import `in`.surajsau.jisho.data.model.Screen
import `in`.surajsau.jisho.ui.Destinations
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToDestination: (Destinations) -> Unit
) {

    val (state, _) = use(LocalHomeViewModel.current)

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(state.screens) { index, screen ->

                ScreenCard(
                    screen = screen,
                    onClick = { navigateToDestination.invoke(it) },
                    modifier = Modifier.padding(
                        top = if (index == 0) 16.dp else 0.dp,
                        bottom = 16.dp
                    )
                )
            }
        }
    }
}

@Composable
fun ScreenCard(
    screen: Screen,
    modifier: Modifier = Modifier,
    onClick: (Destinations) -> Unit
) {
    val context = LocalContext.current

    val painter = rememberImagePainter(
        data = screen.previewImage,
        imageLoader = ImageLoader.invoke(context).newBuilder()
            .componentRegistry {
                val decoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoderDecoder(LocalContext.current)
                } else {
                    GifDecoder()
                }
                add(decoder)
            }
            .build()
    )
    
    Row(
        modifier = modifier
            .clickable { onClick.invoke(screen.destinations) }
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clipToBounds()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(2f)
                .padding(16.dp)
        ) {
            Text(
                text = screen.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Text(
                text = screen.description,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}