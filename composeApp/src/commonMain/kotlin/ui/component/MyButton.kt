package ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import domain.enums.PlatformType
import getPlatform
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.theme.MyAppTheme

@Composable
fun DeleteIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        modifier = modifier.then(Modifier.size(40.dp)),
        onClick = {
            onClick()
        },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            tint = Color.White,
            contentDescription = "Delete"
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClick() },
        content = {
            Icon(
                painter = rememberVectorPainter(
                    image = if (getPlatform().type == PlatformType.ANDROID) {
                        Icons.AutoMirrored.Filled.ArrowBack
                    } else Icons.AutoMirrored.Filled.KeyboardArrowLeft
                ),
                contentDescription = "Back"
            )
        }
    )
}

@Preview
@Composable
fun PrevMyBackButton() {
    MyAppTheme {
        BackButton {  }
    }
}