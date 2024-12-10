package ui.component.gym

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import ui.component.colorPalette.ColorChooser

@Composable
fun WorkoutPlanPersonalization(
    modifier: Modifier = Modifier,
    onEditRequest: () -> Unit = {},
    onDeleteRequest: () -> Unit = {},
    onColorChangeRequest: (selectedColorHex: String, selectedColor: Color) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onEditRequest()
                }
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = rememberVectorPainter(
                    image = Icons.Default.Edit,
                ),
                contentDescription = "Edit",
            )
            Spacer(Modifier.width(8.dp))
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Edit",
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = "Warna",
            )
            ColorChooser(selectedColor = "#B68AFF") { selectedHex, selectedColor ->
                onColorChangeRequest.invoke(selectedHex, selectedColor)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    onDeleteRequest()
                }
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.errorContainer),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = rememberVectorPainter(
                    image = Icons.Default.Delete,
                ),
                tint = MaterialTheme.colorScheme.error,
                contentDescription = "delete",
            )
            Spacer(Modifier.width(8.dp))
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Hapus",
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}