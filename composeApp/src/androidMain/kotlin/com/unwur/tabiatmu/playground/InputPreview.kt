package com.unwur.tabiatmu.playground

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ui.component.InputNumber

@Preview(showBackground = true)
@Composable
private fun InputPrev() {
    MaterialTheme {
        InputNumber(Modifier)
    }
}