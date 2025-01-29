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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import domain.enums.PlatformType
import getPlatform
import org.jetbrains.compose.resources.painterResource
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.ic_edit_pencil_icon_32dp
import tabiat.composeapp.generated.resources.ic_xmark_icon_32dp

@Composable
fun DeleteIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        modifier = modifier.then(Modifier.size(40.dp)),
        onClick = {
            onClick()
        },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.error,
        ),
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            tint = MaterialTheme.colorScheme.onError,
            contentDescription = "Delete",
        )
    }
}

@Composable
fun AddIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(30.dp),
        onClick = {
            onClick()
        },
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = CircleShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Default.Add,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Add Workout Plan"
            )
        }
    }
}

@Composable
fun EditIconButton(
    modifier: Modifier = Modifier,
    editMode: Boolean,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            if (editMode) {
                onEditClick()
            } else {
                onCancelClick()
            }
        },
        colors = colors,
    ) {
        AnimatedContent(
            targetState = editMode,
            label = "animated-tabiat-edit-transition",
            transitionSpec = { imageButtonTransitionSpec }
        ) { iconEditMode ->
            Icon(
                modifier = Modifier.size(24.dp),
                painter = if (iconEditMode)
                    painterResource(Res.drawable.ic_edit_pencil_icon_32dp)
                else painterResource(Res.drawable.ic_xmark_icon_32dp),
                contentDescription = if (iconEditMode) "Edit" else "Cancel"
            )
        }
    }
}

@Composable
fun CloseIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = {
            onClick.invoke()
        },
        colors = colors,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(Res.drawable.ic_xmark_icon_32dp),
            contentDescription = "Close"
        )
    }
}

private val imageButtonTransitionSpec =
    (scaleIn(
        initialScale = 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
    )).togetherWith(
        scaleOut(animationSpec = tween(90))
    )

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showBackground: Boolean = false,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClick() },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (showBackground) MaterialTheme.colorScheme.background else Color.Transparent,
        ),
        content = {
            Icon(
                painter = rememberVectorPainter(
                    image = if (getPlatform().type == PlatformType.ANDROID) {
                        Icons.AutoMirrored.Filled.ArrowBack
                    } else {
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft
                    },
                ),
                contentDescription = "Back",
            )
        },
    )
}

@Composable
fun MyPrimaryButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textContent: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        onClick = {
            onClick()
        },
    ) {
        textContent()
    }
}
