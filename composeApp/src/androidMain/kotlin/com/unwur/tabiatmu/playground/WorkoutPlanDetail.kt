package com.unwur.tabiatmu.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.add_2_24px
import tabiat.composeapp.generated.resources.close_24px
import com.unwur.tabiatmu.ui.theme.MyAppTheme

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
                Icon(
//                    imageVector = Icons.Default.Add,
                    painter = painterResource(Res.drawable.add_2_24px),
                    contentDescription = ""
                )
            }
            IconButton(onClick = {  }) {
                Icon(
//                    imageVector = Icons.Default.Close,
                    painter = painterResource(Res.drawable.close_24px),
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