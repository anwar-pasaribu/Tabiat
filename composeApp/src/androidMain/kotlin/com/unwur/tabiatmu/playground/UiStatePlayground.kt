package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.EmptyState
import ui.theme.MyAppTheme

@Preview(showBackground = true)
@Composable
private fun EmptyStatePrev() {
    MyAppTheme {
        Column {
            EmptyState(
                modifier = Modifier.padding(16.dp),
                title = "Empty State",
                btnText = "Buat Baru"
            )
        }
    }
}