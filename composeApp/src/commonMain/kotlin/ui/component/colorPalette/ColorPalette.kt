package ui.component.colorPalette

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

fun String?.parseHexToComposeColor(): Color {
    if (this.isNullOrBlank()) return Color.Unspecified
    return Color(
        this.removePrefix("#").toLong(16) or 0x00000000FF000000
    )
}

@Composable
fun ColorChooser(
    modifier: Modifier = Modifier,
    selectedColor: String = "",
    onColorSelected: (selectedColorHex: String, selectedColor: Color) -> Unit
) {
    var selectedColorString by remember {
        mutableStateOf("")
    }

    LaunchedEffect(selectedColor) {
        selectedColorString = selectedColor
    }

    Column(modifier = modifier) {
        val colors = listOf(
            "#36693E",
            "#00d9ff",
            "#65a8ff",
            "#B68AFF",
            "#E44CFF",
            "#FF689F",
            "#FF877C",
            "#FFA074",
            "#FFC369",
            "#FFD765",
            "#FFF883",
            "#E7F280",
            "#A6DF81",
            "#ACC3A7",
            "#99B8BD",
            "#BCA7B8",
            "#BBB1A7",
            "#000000",
        )

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            state = rememberLazyGridState(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            columns = GridCells.Fixed(6),
        ) {
            items(18) {
                Box(modifier = Modifier.wrapContentHeight()) {
                    val colorString = colors[it]
                    val color = colorString.parseHexToComposeColor()
                    val colorSelected = colorString == selectedColorString
                    val animatedCornerRadius by animateFloatAsState(
                        targetValue = if (colorSelected) 1F else 0F,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "color-palette-animated-shape"
                    )
                    val shape = lerp(16.dp, 28.dp, animatedCornerRadius)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(shape))
                            .clickable {
                                selectedColorString = colorString
                                onColorSelected.invoke(colorString, color)
                            }
                            .size(56.dp)
                            .background(color)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(shape)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }

}