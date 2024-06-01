package ui.component.gym

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
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

@OptIn(ExperimentalFoundationApi::class, FlowPreview::class)
@Composable
fun ExerciseSearchView(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
    onClearQuery: () -> Unit = {}
) {
    val queryState = rememberTextFieldState(initialText = "")

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val searchInteractionSource = remember { MutableInteractionSource() }
    val focused by searchInteractionSource.collectIsFocusedAsState()
    LaunchedEffect(searchInteractionSource) {
        searchInteractionSource.interactions.collectLatest {
            if (it is FocusInteraction.Focus) {
                println("FOCUS YES")
            } else if(it is FocusInteraction.Unfocus) {
                println("FOCUS NO")
            }
        }
    }

    LaunchedEffect(queryState) {
        queryState.textAsFlow()
            .debounce(600)
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
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField2(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 16.dp, end = 4.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary
                ),
                state = queryState,
                interactionSource = searchInteractionSource,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = if (queryState.text.isNotEmpty()) ImeAction.Search else ImeAction.Default
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onSearch(queryState.text.toString())
                    }
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                lineLimits = TextFieldLineLimits.SingleLine,
                decorator = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Search Icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (queryState.text.isEmpty()) {
                                Text(
                                    text = "Cari latihan...",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        textAlign = TextAlign.Start,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F)
                                    )
                                )
                            }
                            innerTextField()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedVisibility(
                            visible = queryState.text.isNotEmpty(),
                            enter = EnterTransition.None,
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = {
                                    queryState.clearText()
                                    onClearQuery()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Clear search text"
                                )
                            }
                        }

                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}