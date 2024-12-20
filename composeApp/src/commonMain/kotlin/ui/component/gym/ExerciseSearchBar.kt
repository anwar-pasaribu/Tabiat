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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun ExerciseSearchView(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
    onClearQuery: () -> Unit = {},
) {
    val queryState = remember { TextFieldState( initialText = "") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val searchInteractionSource = remember { MutableInteractionSource() }
    val focused by searchInteractionSource.collectIsFocusedAsState()
    LaunchedEffect(searchInteractionSource) {
        searchInteractionSource.interactions.collectLatest {
            if (it is FocusInteraction.Focus) {
                println("FOCUS YES")
            } else if (it is FocusInteraction.Unfocus) {
                println("FOCUS NO")
            }
        }
    }

    LaunchedEffect(queryState) {
        snapshotFlow { queryState.text }
            .debounce(300)
            .collectLatest {
                if (focused) {
                    onQueryChange(it.toString())
                }
            }
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 16.dp, end = 4.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary,
                ),
                state = queryState,
                interactionSource = searchInteractionSource,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = if (queryState.text.isNotEmpty()) ImeAction.Search else ImeAction.Default
                ),
                onKeyboardAction = { imeAction ->
                    println("Ime: $imeAction")
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onSearch(queryState.text.toString())
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                lineLimits = TextFieldLineLimits.SingleLine,
                decorator = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Search Icon",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (queryState.text.isEmpty()) {
                                Text(
                                    text = "Cari latihan...",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        textAlign = TextAlign.Start,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
                                    ),
                                )
                            }
                            innerTextField()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedVisibility(
                            visible = queryState.text.isNotEmpty(),
                            enter = EnterTransition.None,
                            exit = fadeOut(),
                        ) {
                            IconButton(
                                onClick = {
                                    queryState.clearText()
                                    onClearQuery()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Clear search text",
                                )
                            }
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
