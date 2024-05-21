package com.unwur.tabiatmu.playground

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import domain.model.gym.GymPreferences
import features.settings.SettingPage
import ui.theme.MyAppTheme

private val gymOptionList = listOf(
    GymPreferences(setTimerDuration = 15, breakTimeDuration = 5),
    GymPreferences(setTimerDuration = 30, breakTimeDuration = 7),
    GymPreferences(setTimerDuration = 45, breakTimeDuration = 9),
    GymPreferences(setTimerDuration = 60, breakTimeDuration = 10),
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun WorkoutListItemViewPreview() {
    MyAppTheme {
        Column {
            SettingPage(
                gymPreferences = GymPreferences(45, 5),
                exerciseTimerOptions = gymOptionList
            )
        }
    }
}
