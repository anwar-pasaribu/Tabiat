package ui.component.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.compose_multiplatform
import ui.component.ImageWrapper


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExerciseListItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Surface {
        Card {
            Row(modifier = Modifier.clickable {
                onClick()
            }) {
                ImageWrapper(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(56.dp)
                        .align(Alignment.CenterVertically),
                    resource = Res.drawable.compose_multiplatform,
                    contentDescription = "Picture of $title"
                )

                Box(
                    modifier = modifier.then(
                        Modifier
                            .fillMaxWidth()
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
}