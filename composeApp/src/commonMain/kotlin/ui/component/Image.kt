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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ImageWrapper(
    modifier: Modifier = Modifier,
    resource: DrawableResource,
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
    } else {
        Image(
            modifier = modifier,
            alignment = alignment,
            painter = painterResource(resource),
            contentScale = contentScale,
            contentDescription = contentDescription,
            colorFilter = colorFilter
        )
    }
}