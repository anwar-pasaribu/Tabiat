package com.unwur.tabiatmu.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ui.theme.MyAppTheme

@Composable
fun WorkoutPlanDetailHeader(modifier: Modifier = Modifier) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Magenta),
    ) {
        Text(
            text = "Long text here ",
            style = MaterialTheme.typography.headlineSmall,
        )

        Row {
            IconButton(
                modifier = Modifier,
                onClick = { },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                )
            }
        }
    }
}

@Preview
@Composable
private fun WorkoutPlanDetailHeaderPrev() {

    MyAppTheme {
        WorkoutPlanDetailHeader()
    }

}