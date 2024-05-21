package com.unwur.tabiatmu.playground

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import ui.component.gym.TimerDisplay
import ui.theme.MyAppTheme


@Preview(showBackground = true,
    device = "id:Nexus 5",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
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
            onCancelTimer = {}
        )
    }
}

@Preview(showBackground = true,
    device = "id:Nexus 5",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun TimerDisplayBreakTime() {
    MyAppTheme {
        val countDown = remember {
            mutableIntStateOf(45)
        }
        TimerDisplay(
            countDown = countDown.intValue,
            breakTime = true,
            onTimerFinished = {
                println("TIMER COMPLETED")
                countDown.intValue = 5
            },
            onCancelTimer = {}
        )
    }
}