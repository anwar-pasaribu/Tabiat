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
package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.ExerciseSetItemView
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun ExerciseSetItemViewPrev() {
    MyAppTheme {
        Card {
            Column {
                repeat(5) {
                    ExerciseSetItemView(
                        modifier = Modifier,
                        it,
                        listOf(12, 15, 16, 6, 7).random(),
                        listOf(12, 16, 18, 20, 26).random(),
                        finished = listOf(false, true).random(),
                        onSetItemClick = {},
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ExerciseSetItemView(
                    modifier = Modifier,
                    9,
                    listOf(12, 15, 16, 6, 7).random(),
                    listOf(12, 16, 18, 20, 26).random(),
                    finished = listOf(false, true).random(),
                    onSetItemClick = {},
                    stateIcon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                    },
                )
            }
        }
    }
}
