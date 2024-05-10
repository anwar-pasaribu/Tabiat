package ui.component.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun WorkoutListItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Surface {
        Card {
            Box(
                modifier = modifier.then(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick()
                        }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                )
            ) {

                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = rememberVectorPainter(
                            image = Icons.AutoMirrored.Filled.KeyboardArrowRight
                        ),
                        contentDescription = "See more: $title"
                    )
                }
            }
        }
    }
}