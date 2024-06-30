package ui.component.gym

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.full_screen_24dp
import ui.component.ImageWrapper

@Composable
fun ExerciseListItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    image: String = "",
    imageUrlList: List<String> = emptyList(),
    selected: Boolean = false,
    enabled: Boolean = true,
    highlightedText: String = "",
    onClick: () -> Unit = {}
) {
    val imageAvailable = image.isNotEmpty() || imageUrlList.isNotEmpty()
    val imageUrl = remember { image.ifEmpty { imageUrlList.getOrNull(0) }.orEmpty() }
    var bigPictureMode by rememberSaveable { mutableStateOf(false) }

    val animatedFloat = Animatable(1f)
    LaunchedEffect(bigPictureMode) {
        animatedFloat.animateTo(
            targetValue = if (bigPictureMode) 0f else 1f,
            animationSpec = tween(durationMillis = 150)
        )
    }

    if (bigPictureMode) {
        FullScreenImageViewer(
            imageUrlList = imageUrlList,
            onBack = {
                bigPictureMode = false
            }
        )
    }
    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) Color.Green else Color.Transparent
        ),
    ) {
        Row(
            modifier = Modifier.clickable(enabled = enabled) { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageAvailable) {
                Box(modifier = Modifier
                    .alpha(animatedFloat.value)
                    .clickable {
                        bigPictureMode = !bigPictureMode
                    }
                    .size(128.dp)
                    .align(Alignment.CenterVertically)
                ) {
                    var loadingIndicatorVisible by remember { mutableStateOf(true) }
                    ImageWrapper(
                        modifier = Modifier
                            .width(128.dp)
                            .height(128.dp)
                            .align(Alignment.Center),
                        imageUrl = imageUrl,
                        contentDescription = "Picture of $title",
                        contentScale = ContentScale.Crop,
                        onLoading = { loadingIndicatorVisible = true },
                        onLoadSuccess = { loadingIndicatorVisible = false }
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        visible = loadingIndicatorVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            Modifier.size(128.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = .35F))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp, top = 6.dp)
                            .align(Alignment.TopEnd),
                    ) {
                        ImageWrapper(
                            modifier = Modifier
                                .offset(x = (-1).dp, y = (1).dp)
                                .align(Alignment.Center),
                            resource = Res.drawable.full_screen_24dp,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                        ImageWrapper(
                            modifier = Modifier
                                .align(Alignment.Center),
                            resource = Res.drawable.full_screen_24dp,
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Column(modifier = Modifier.padding(end = 32.dp, top = 6.dp, bottom = 6.dp)) {
                    if (highlightedText.isEmpty()) {
                        Text(text = title, style = MaterialTheme.typography.titleLarge)
                    } else {
                        HighlightText(text = title, highlightedText = highlightedText)
                    }
                    Text(text = description, style = MaterialTheme.typography.labelMedium)
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(if (enabled) 1f else 0f),
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

@Composable
fun HighlightText(text: String, highlightedText: String) {
    val annotatedString = remember(text, highlightedText) {
        buildAnnotatedString {
            val startIndex = text.indexOf(highlightedText, ignoreCase = true)
            if (startIndex != -1) {
                append(text.substring(0, startIndex))
                withStyle(style = SpanStyle(background = Color.Yellow.copy(alpha = .5F))) {
                    append(text.substring(startIndex, startIndex + highlightedText.length))
                }
                append(text.substring(startIndex + highlightedText.length))
            } else {
                append(text)
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
fun WorkoutExerciseItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String = "",
    imageUrlList: List<String> = emptyList(),
    selected: Boolean = false,
    enabled: Boolean = true,
    progressContentView: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    val imageAvailable = imageUrlList.isNotEmpty()
    val imageUrl = remember { imageUrlList.getOrNull(0).orEmpty() }
    var bigPictureMode by rememberSaveable { mutableStateOf(false) }

    val animatedFloat = Animatable(1f)
    LaunchedEffect(bigPictureMode) {
        animatedFloat.animateTo(
            targetValue = if (bigPictureMode) 0f else 1f,
            animationSpec = tween(durationMillis = 150)
        )
    }

    if (bigPictureMode) {
        FullScreenImageViewer(
            imageUrlList = imageUrlList,
            onBack = {
                bigPictureMode = false
            }
        )
    }
    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) Color.Green else Color.Transparent
        ),
    ) {
        Row(modifier = Modifier.clickable(enabled = enabled) {
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
                    Spacer(Modifier.height(8.dp))
                    progressContentView()
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(if (enabled) 1f else 0f),
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

@Composable
fun FullScreenImageViewer(
    modifier: Modifier = Modifier,
    imageUrlList: List<String>,
    onBack: () -> Unit
) {
    Dialog(
        onDismissRequest = { onBack() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier.then(Modifier.fillMaxSize()),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onBack() }
                ).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ImagePager(modifier = Modifier.fillMaxWidth(), imageUrlList)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    imageUrlList: List<String>,
    pageSpacing: Dp = 4.dp,
    fillWidth: Boolean = false,
    contentScale: ContentScale = ContentScale.FillWidth,
    shape: Shape = RoundedCornerShape(6.dp),
    onPageChange: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        imageUrlList.size
    }
    val contentPaddingValues = if (imageUrlList.size == 1 || fillWidth) {
        PaddingValues(0.dp)
    } else {
        PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            onPageChange(it)
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = contentPaddingValues,
        pageSpacing = pageSpacing
    ) {
        ImageWrapper(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            ).clip(shape).fillMaxWidth(),
            imageUrl = imageUrlList[it],
            contentDescription = "Picture $it",
            contentScale = contentScale
        )
    }
}