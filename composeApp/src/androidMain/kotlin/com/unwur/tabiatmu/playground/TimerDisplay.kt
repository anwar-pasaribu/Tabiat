package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.TimerDisplay
import ui.theme.MyAppTheme


@Preview
@Composable
private fun TimerDisplayPrev() {
    MyAppTheme {
        TimerDisplay(
            countDown = 15,
            onTimerFinished = {
                println("TIMER COMPLETED")
            },
            onCancelTimer = {}
        )
    }
}