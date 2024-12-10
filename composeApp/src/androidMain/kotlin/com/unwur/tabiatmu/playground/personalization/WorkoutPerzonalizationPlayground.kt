package com.unwur.tabiatmu.playground.personalization

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ui.component.gym.WorkoutPlanPersonalization
import ui.theme.MyAppTheme

@Composable
@PreviewLightDark
fun DeleteButtonPreview() {
    MyAppTheme {
        Surface {
            Column {
                WorkoutPlanPersonalization()
            }
        }
    }
}

