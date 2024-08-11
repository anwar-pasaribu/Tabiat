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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.LatestExercise
import ui.component.gym.WorkoutPlanItemView
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout",
                description = "Workout description",
                progress = 7,
                total = 10,
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout 2",
                description = "Workout description panjang",
                progress = 10,
                total = 10,
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout without desc",
                description = "",
            )
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutPlanItemView(
                modifier = Modifier,
                title = "Workout with last activity",
                description = "Description",
                progress = 9,
                total = 10,
                lastActivityInfo = {
                    LatestExercise(
                        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                        exerciseImageUrl = "",
                        upperLabel = "Latihan terakhir 12 Juni 2024",
                        lowerLabel = "Barbell Bench Press (10 x 12kg)",
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun LatestExercisePrev() {
    MyAppTheme {
        LatestExercise(
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
            exerciseImageUrl = "",
            upperLabel = "Push ups",
            lowerLabel = "10",
        )
    }
}
