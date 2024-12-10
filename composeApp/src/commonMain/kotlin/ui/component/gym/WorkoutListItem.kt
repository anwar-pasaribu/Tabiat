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
package ui.component.gym

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import features.exerciseList.BottomSheet
import ui.component.ImageWrapper
import ui.component.InsetNavigationHeight
import ui.component.colorPalette.parseHexToComposeColor

fun Color.bestContrastColor(): Color {
    val luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue)
    return if (luminance > 0.5) Color.Black else Color.White
}

@Composable
fun WorkoutPlanItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    lastActivityInfo: @Composable() (RowScope.() -> Unit)? = null,
    total: Int = 0,
    progress: Int = 0,
    onClick: () -> Unit = {},
    onEditRequest: () -> Unit = {},
    onDeleteRequest: () -> Unit = {},
    onChangeColorRequest: (colorHex: String) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
) {

    var menuVisible by remember { mutableStateOf(false) }
    var temporarySelectedColorHex by rememberSaveable { mutableStateOf("") }
    val workoutSelectedColor by remember {
        derivedStateOf {
            if (temporarySelectedColorHex.isNotEmpty()) {
                temporarySelectedColorHex.parseHexToComposeColor()
            } else {
                backgroundColor
            }
        }
    }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = workoutSelectedColor,
        label = "workout-plan-card-color"
    )
    val textColorBasedOnBackgroundColor by remember {
        derivedStateOf {
            animatedBackgroundColor.bestContrastColor()
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = animatedBackgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Box(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .heightIn(min = 132.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = title,
                    color = textColorBasedOnBackgroundColor,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        color = textColorBasedOnBackgroundColor,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                    )
                }
            }

            WorkoutPlanProgressIndicator(
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp).size(size = 40.dp),
                total = total,
                progress = progress,
            )

            if (lastActivityInfo != null) {
                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                ) {
                    lastActivityInfo()
                }
            }

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { menuVisible = true },
            ) {
                Icon(
                    painter = rememberVectorPainter(
                        image = Icons.Default.MoreVert,
                    ),
                    contentDescription = "More menu",
                )
            }
        }

    }

    if (menuVisible) {
        BottomSheet(
            onDismiss = { menuVisible = false },
            showFullScreen = false,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                WorkoutPlanPersonalization(
                    onEditRequest = {
                        menuVisible = false
                        onEditRequest.invoke()
                    },
                    onDeleteRequest = {
                        menuVisible = false
                        onDeleteRequest.invoke()
                    },
                    onColorChangeRequest = { selectedColorHex, selectedColor ->
                        temporarySelectedColorHex = selectedColorHex
                        onChangeColorRequest.invoke(selectedColorHex)
                    }
                )
                InsetNavigationHeight()
                InsetNavigationHeight()
            }
        }
    }
}

@Composable
private fun WorkoutPlanProgressIndicator(
    modifier: Modifier,
    total: Int,
    progress: Int,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (total > progress) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 40.dp).padding(4.dp),
                progress = { progress / total.toFloat() },
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .25F),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
        } else if (total != 0 && progress != 0) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Finished",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun LatestExercise(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onPrimary,
    exerciseImageUrl: String,
    upperLabel: String,
    lowerLabel: String,
) {
    val exerciseImageCLipSize = 36.dp
    val outerShape = remember {
        RoundedCornerShape(10.dp)
    }
    val innerShape = remember {
        RoundedCornerShape(8.dp)
    }
    val textColorBasedOnBackgroundColor by remember {
        derivedStateOf {
            backgroundColor.bestContrastColor()
        }
    }

    Row(
        modifier = modifier.then(
            Modifier
                .background(
                    color = backgroundColor.copy(alpha = .15F),
                    shape = outerShape,
                )
                .heightIn(min = 40.dp)
                .padding(end = 8.dp),
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(2.dp))
        Card(Modifier.size(exerciseImageCLipSize), shape = innerShape) {
            ImageWrapper(
                modifier = Modifier
                    .clip(innerShape)
                    .size(exerciseImageCLipSize),
                contentDescription = "",
                imageUrl = exerciseImageUrl,
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = upperLabel,
                color = textColorBasedOnBackgroundColor,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = lowerLabel,
                color = textColorBasedOnBackgroundColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
