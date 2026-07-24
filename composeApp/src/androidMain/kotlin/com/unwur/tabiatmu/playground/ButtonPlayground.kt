package com.unwur.tabiatmu.playground

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.unwur.tabiatmu.ui.component.DeleteIconButton
import com.unwur.tabiatmu.ui.theme.MyAppTheme

@Composable
@PreviewLightDark
fun DeleteButtonPreview() {
    MyAppTheme {
        DeleteIconButton {  }
    }
}