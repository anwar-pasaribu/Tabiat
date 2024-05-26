package ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageWrapper(
    modifier: Modifier = Modifier,
    resource: DrawableResource? = null,
    imageUrl: String = "",
    colorFilter: ColorFilter? = null,
    alignment: Alignment = Alignment.CenterStart,
    contentScale: ContentScale = ContentScale.FillHeight,
    contentDescription: String,
    onLoading: () -> Unit = {},
    onLoadSuccess: (MemoryCache.Key) -> Unit = {}
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
        val model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()

        AsyncImage(
            modifier = modifier,
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            onState = {
                if(it is AsyncImagePainter.State.Loading) {
                    onLoading()
                } else if (it is AsyncImagePainter.State.Success) {
                    it.result.memoryCacheKey?.let { cacheKey -> onLoadSuccess(cacheKey) }
                }
            }
        )
    }
}