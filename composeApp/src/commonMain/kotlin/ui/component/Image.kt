package ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImageWrapper(
    modifier: Modifier = Modifier,
    resource: DrawableResource? = null,
    imageUrl: String = "",
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.CenterStart,
    contentScale: ContentScale = ContentScale.FillHeight,
    contentDescription: String
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.then(
                Modifier.clip(
                    RoundedCornerShape(8.dp)
                ).size(24.dp).background(Color.LightGray)
            )
        )
    } else if (resource != null) {
        Image(
            modifier = modifier,
            alignment = alignment,
            painter = painterResource(resource),
            contentScale = contentScale,
            contentDescription = contentDescription,
            colorFilter = colorFilter
        )
    } else if(imageUrl.isNotEmpty()) {
        // Keep track of the image's memory cache key so it can be used as a placeholder
        // for the detail screen.
        var placeholder: MemoryCache.Key? = remember { null }
        val painterState = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(imageUrl)
                .size(Size.ORIGINAL)
                .crossfade(true)
                .build(),
            onState = {
                if (it is AsyncImagePainter.State.Success) {
                    placeholder = it.result.memoryCacheKey
                }
            }
        )

        Image(
            modifier = modifier,
            alignment = alignment,
            painter = painterState,
            contentScale = contentScale,
            contentDescription = contentDescription,
            colorFilter = colorFilter
        )
    }
}