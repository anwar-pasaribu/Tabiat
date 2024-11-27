package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ui.component.DeleteIconButton
import ui.theme.MyAppTheme

@Composable
@PreviewLightDark
fun DeleteButtonPreview() {
    MyAppTheme {
        DeleteIconButton {  }
    }
}