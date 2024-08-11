/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
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
    onLoadSuccess: (MemoryCache.Key) -> Unit = {},
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.then(
                Modifier.clip(
                    RoundedCornerShape(8.dp),
                ).size(24.dp).background(Color.LightGray),
            ),
        )
    } else if (resource != null) {
        Image(
            modifier = modifier,
            alignment = alignment,
            painter = painterResource(resource),
            contentScale = contentScale,
            contentDescription = contentDescription,
            colorFilter = colorFilter,
        )
    } else if (imageUrl.isNotEmpty()) {
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
                if (it is AsyncImagePainter.State.Loading) {
                    onLoading()
                } else if (it is AsyncImagePainter.State.Success) {
                    it.result.memoryCacheKey?.let { cacheKey -> onLoadSuccess(cacheKey) }
                }
            },
        )
    }
}
