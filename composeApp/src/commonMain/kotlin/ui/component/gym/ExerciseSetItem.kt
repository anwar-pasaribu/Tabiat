package ui.component.gym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseSetItemView(
    modifier: Modifier = Modifier,
    setNumber: Int,
    setCount: Int = 0,
    setWeight: Int = 0,
    onSetItemClick: () -> Unit
) {

    Row(modifier = modifier.then(
        Modifier
            .clickable {
                onSetItemClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth())) {

        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest,
                    CircleShape
                )
                .size(40.dp), contentAlignment = Alignment.Center
        ) {
            Text(text = "$setNumber")
        }

        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = "$setCount ✕ $setWeight kg",
            style = MaterialTheme.typography.titleLarge
        )

        Box (modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth()){
            Icon(
                modifier = Modifier.align(Alignment.CenterEnd),
                painter = rememberVectorPainter(
                    image = Icons.AutoMirrored.Filled.KeyboardArrowRight
                ),
                contentDescription = "Back"
            )
        }

    }
}