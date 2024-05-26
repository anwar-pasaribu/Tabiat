package ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySection(
    modifier: Modifier = Modifier,
    exerciseCategoryList: List<String>,
    onSelectCategory: (category: String) -> Unit,
    onClearCategoryFilter: () -> Unit
) {

    var selectedCategory by remember {
        mutableStateOf("")
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory.isNotEmpty()) {
            onSelectCategory(selectedCategory)
        } else {
            onClearCategoryFilter()
        }
    }

    Row(modifier = modifier.then(Modifier.horizontalScroll(
        state = rememberScrollState()
    )),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(Modifier.width(8.dp))
        exerciseCategoryList.forEach {
            InputChip(
                selected = it == selectedCategory,
                onClick = {
                    selectedCategory = if (it == selectedCategory) {
                        ""
                    } else {
                        it
                    }
                },
                colors = InputChipDefaults.inputChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = CircleShape,
                label = {
                    Text(text = it)
                }
            )
        }
        Spacer(Modifier.width(8.dp))
    }
}