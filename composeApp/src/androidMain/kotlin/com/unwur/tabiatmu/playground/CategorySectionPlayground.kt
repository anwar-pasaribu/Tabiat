package com.unwur.tabiatmu.playground

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ui.component.CategorySection
import ui.theme.MyAppTheme


@Preview(showBackground = true)
@Composable
private fun CategorySectionPrev() {
    val categoryList = listOf("arm", "chest", "back", "arm", "chest", "back")
    MyAppTheme {
        CategorySection(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            exerciseCategoryList = categoryList,
            onSelectCategory = {},
            onClearCategoryFilter = {}
        )
    }
}