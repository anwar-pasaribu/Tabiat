package ui.component.gym

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseSetBadge(
    modifier: Modifier = Modifier,
    textContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier.then(
            Modifier
                .wrapContentWidth()
                .border(1.dp, Color.LightGray.copy(alpha = .75F), RoundedCornerShape(25))
                .padding(start = 4.dp, end = 4.dp)
        )
    ) {
        textContent()
    }
}