package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.gym.ExerciseSearchView
import ui.theme.MyAppTheme


@Preview(showBackground = true)
@Composable
private fun ExerciseSearchViewPrev() {
    MyAppTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ExerciseSearchView(
                modifier = Modifier.fillMaxWidth(),
                query = "", onQueryChange = {}, onSearch = {})
        }
    }
}