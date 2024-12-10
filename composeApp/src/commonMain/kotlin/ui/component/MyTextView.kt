package ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun MainHeaderText(
    modifier: Modifier = Modifier,
    headerStyle: TextStyle = MaterialTheme.typography.headlineSmall,
    color: Color = Color.Unspecified,
    textTitle: CharSequence
) {
    AnimatedContent(
        targetState = textTitle,
        transitionSpec = {
            fadeIn().togetherWith(
                fadeOut()
            )
        }
    ) {
        Text(
            modifier = modifier,
            color = color,
            text = it.toString(),
            style = headerStyle,
        )
    }
}