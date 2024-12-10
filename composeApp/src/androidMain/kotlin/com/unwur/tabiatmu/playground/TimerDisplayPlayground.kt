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

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.TimerDisplay
import ui.component.gym.TimerDisplayFloating
import ui.theme.MyAppTheme

@Preview(
    showBackground = true,
    device = "id:Nexus 5",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun TimerDisplayPrev() {
    MyAppTheme {
        val countDown = remember {
            mutableIntStateOf(45)
        }
        TimerDisplay(
            countDown = countDown.intValue,
            breakTime = false,
            onTimerFinished = {
                println("TIMER COMPLETED")
                countDown.intValue = 5
            },
            onCancelTimer = {},
        )
    }
}

@Preview(
    showBackground = true,
    device = "id:Nexus 5",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun TimerDisplayBreakTime() {
    MyAppTheme {
        val countDown = remember {
            mutableIntStateOf(300)
        }
        TimerDisplay(
            modifier = Modifier.background(Color.Red),
            countDown = countDown.intValue,
            breakTime = true,
            onTimerFinished = {
                println("TIMER COMPLETED")
                countDown.intValue = 5
            },
            onCancelTimer = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerDisplaySmallPrev() {
    MyAppTheme {
        val countDown = remember {
            mutableIntStateOf(45)
        }
        TimerDisplayFloating(
            countDown = countDown.intValue,
            countDownInitial = 50,
        )
    }
}
