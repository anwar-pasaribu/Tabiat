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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseSetItemView(
    modifier: Modifier = Modifier,
    setNumber: Int,
    setCount: Int = 0,
    setWeight: Int = 0,
    finished: Boolean = false,
    stateIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onSetItemClick: () -> Unit,
    onSetItemLongClick: () -> Unit = {},
) {
    val rowBackgroundColor = if (finished) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    val setNumberCircleBackground = if (finished) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    Row(
        modifier = modifier.then(
            Modifier
                .combinedClickable(
                    enabled = enabled,
                    onClick = {
                        onSetItemClick()
                    },
                    onLongClick = {
                        onSetItemLongClick()
                    },
                )
                .background(rowBackgroundColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
        ),
    ) {
        Box(
            modifier = Modifier
                .background(
                    setNumberCircleBackground,
                    CircleShape,
                )
                .size(40.dp)
                .border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "$setNumber", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "$setCount ✕ $setWeight kg",
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (finished) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            ),
        )

        Box(
            modifier = Modifier
                .alpha(if (enabled) 1F else 0F)
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (stateIcon == null) {
                val icon = if (finished) {
                    Icons.Default.Done
                } else {
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                }
                Icon(
                    painter = rememberVectorPainter(
                        image = icon,
                    ),
                    contentDescription = "",
                    tint = if (finished) MaterialTheme.colorScheme.onPrimary else IconButtonDefaults.iconButtonColors().contentColor,
                )
            } else {
                stateIcon()
            }
        }
    }
}
