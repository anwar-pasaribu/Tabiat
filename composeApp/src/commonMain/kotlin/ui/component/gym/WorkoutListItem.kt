package ui.component.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import features.exerciseList.BottomSheet
import ui.component.InsetNavigationHeight

@Composable
fun WorkoutPlanItemView(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    total: Int = 0,
    progress: Int = 0,
    onClick: () -> Unit = {},
    onEditRequest: () -> Unit = {},
    onDeleteRequest: () -> Unit = {},
) {
    var menuVisible by remember { mutableStateOf(false) }

    Surface {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeightIn(min = 128.dp)
                    .clickable {
                        onClick()
                    }
            ) {

                Column(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    if (description.isNotEmpty()) {
                        Text(text = description, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp).size(size = 40.dp)
                ) {
                    if (total > progress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 40.dp).padding(4.dp),
                            progress = { progress / total.toFloat() },
                            color = MaterialTheme.colorScheme.onPrimary,
                            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .25F),
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    } else if (total != 0 && progress != 0) {
                        Icon(
                            modifier = Modifier.size(40.dp).align(Alignment.Center),
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Finished",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                IconButton(
                    modifier = Modifier
                        .alpha(if (menuVisible) 0f else 1f)
                        .align(Alignment.TopEnd),
                    onClick = { menuVisible = true }
                ) {
                    Icon(
                        painter = rememberVectorPainter(
                            image = Icons.Default.MoreVert
                        ),
                        contentDescription = "More menu"
                    )
                }
            }
        }
    }

    if (menuVisible) {
        BottomSheet(
            onDismiss = { menuVisible = false },
            showFullScreen = false
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onEditRequest()
                        menuVisible = false
                    }.fillMaxWidth().height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        painter = rememberVectorPainter(
                            image = Icons.Default.Edit
                        ),
                        contentDescription = "Edit"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Edit"
                    )
                }
                Row(
                    modifier = Modifier.clickable {
                        onDeleteRequest()
                        menuVisible = false
                    }.fillMaxWidth().height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        painter = rememberVectorPainter(
                            image = Icons.Default.Delete
                        ),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = "delete"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                InsetNavigationHeight()
                InsetNavigationHeight()
            }
        }
    }
}