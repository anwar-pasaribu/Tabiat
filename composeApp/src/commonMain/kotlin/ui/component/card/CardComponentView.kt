package ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.component.CloseIconButton
import ui.extension.bouncingClickable

@Composable
fun vibrantGreenToRedGradient(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
        )
    )
}

@Composable
fun NotificationPermissionStatusCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val cardTextShadow: Shadow = remember {
        Shadow(
            color = Color.Black.copy(alpha = 0.25F),
            offset = Offset(4.0f, 4.0f),
            blurRadius = 8f
        )
    }
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0F),
    ) {
        Box(
            modifier = Modifier
                .bouncingClickable {
                    onClick.invoke()
                }
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .height(132.dp)
                .background(vibrantGreenToRedGradient())
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 16.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = "Notifikasi",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        shadow = cardTextShadow,
                    )
                )
                Text(
                    modifier = Modifier,
                    text = "Klik untuk mengizinkan Tabiat\nmengirim notifikasi 🔔",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        shadow = cardTextShadow,
                    )
                )
            }

            CloseIconButton(
                modifier = Modifier.align(Alignment.TopEnd).alpha(.8F),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSecondary),
                onClick = { onDismiss.invoke() }
            )
        }
    }
}