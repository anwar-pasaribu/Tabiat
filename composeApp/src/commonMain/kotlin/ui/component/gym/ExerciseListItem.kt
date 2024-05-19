package ui.component.gym

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.component.ImageWrapper


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExerciseListItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    image: String = "",
    imageUrlList: List<String> = emptyList(),
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val imageAvailable = image.isNotEmpty() || imageUrlList.isNotEmpty()
    val imageUrl = image.ifEmpty { imageUrlList.getOrNull(0) }.orEmpty()
    var bigPictureMode by rememberSaveable { mutableStateOf(false) }

    val animatedFloat = Animatable(1f)
    LaunchedEffect(bigPictureMode) {
        animatedFloat.animateTo(
            targetValue = if (bigPictureMode) 0f else 1f,
            animationSpec = tween(durationMillis = 150)
        )
    }

    if (bigPictureMode) {
        Dialog(
            onDismissRequest = { bigPictureMode = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { bigPictureMode = false }
                    ).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ImageWrapper(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { }
                        ).fillMaxWidth(),
                        imageUrl = imageUrl,
                        contentDescription = "Big picture of $title",
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }
    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) Color.Green else Color.Transparent
        ),
    ) {
        Row(modifier = Modifier.clickable {
            onClick()
        }) {
            if (imageAvailable) {
                ImageWrapper(
                    modifier = Modifier
                        .alpha(animatedFloat.value)
                        .padding(start = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            bigPictureMode = !bigPictureMode
                        }
                        .size(56.dp)
                        .align(Alignment.CenterVertically),
                    imageUrl = imageUrl,
                    contentDescription = "Picture of $title",
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {

                Column(modifier = Modifier.padding(end = 32.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = rememberVectorPainter(
                            image = if (selected) Icons.Default.Check else Icons.AutoMirrored.Filled.KeyboardArrowRight
                        ),
                        contentDescription = "See more: $title"
                    )
                }
            }
        }
    }
}