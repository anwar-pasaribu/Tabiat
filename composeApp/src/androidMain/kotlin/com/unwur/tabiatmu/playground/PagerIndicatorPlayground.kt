package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import ui.component.PagerIndicator
import ui.theme.MyAppTheme

@Preview
@Composable
private fun PagerIndicatorPrev() {
    MyAppTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                var activePage by remember {
                    mutableStateOf(0)
                }
                PagerIndicator(pageCount = 5, activePage = activePage)
                Button(onClick = {
                    activePage += 1
                    if (activePage >= 5) { // 5 is the pageCount
                        activePage = 0
                    }
                }) {
                    Text(text = "Move Page")
                }
            }
        }
    }
}