package ui.component.gym

import PlayHapticAndSound
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import ui.extension.bouncingClickable
import kotlin.math.absoluteValue

@Composable
fun AddWorkoutLog(modifier: Modifier = Modifier) {
    var selectedReps by remember {
        mutableStateOf(0)
    }
    Card {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Add Log")
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(.5F)) {
                HorizontalScrollSelector(selectedItemIndex = 1, pagerItemList = (1..10).toList()) {
                    selectedReps = it
                }
            }
            Box(modifier = Modifier.weight(.5F)) {
                HorizontalScrollSelector(selectedItemIndex = 0, pagerItemList = (1..10).toList()) {

                }
            }

            Box(modifier = Modifier.weight(.5F)) {
                HorizontalScrollSelector(
                    selectedItemIndex = 2,
                    pagerItemList = (2000..2024).toList()
                ) {

                }
            }

        }
    }
}

@Composable
fun AddExerciseSet(
    modifier: Modifier = Modifier,
    actionText: String = "Tambah Set Latihan",
    initialReps: Int = 0,
    initialWeight: Int = 0,
    onRepsChange: (Int) -> Unit = {},
    onWeightChange: (Int) -> Unit = {},
    addExerciseSetDone: (reps: Int, weight: Int) -> Unit = { _: Int, _: Int -> }
) {
    val repsOptionList = (1..60).toList()
    var selectedReps by remember {
        mutableStateOf(0)
    }

    val initialRepsIndex = if (initialReps == 0) {
        5
    } else {
        repsOptionList.indexOf(initialReps)
    }

    val weightOptionList = (0..300).toList()
    var selectedWeight by remember {
        mutableStateOf(0)
    }
    val initialWeightIndex = if (initialWeight == 0) {
        10
    } else {
        weightOptionList.indexOf(initialWeight)
    }

    // Prevent PlayHapticAndSound on the first time
    var userTriggeredAction by remember { mutableIntStateOf(0) }
    val numberOfFunctioningWheel = 2
    if (userTriggeredAction > numberOfFunctioningWheel) {
        PlayHapticAndSound(selectedReps)
        PlayHapticAndSound(selectedWeight)
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(.4F)) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Repetisi",
                        style = MaterialTheme.typography.labelMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
                HorizontalScrollSelector(
                    selectedItemIndex = initialRepsIndex,
                    pagerItemList = repsOptionList
                ) {
                    selectedReps = repsOptionList[it]
                    onRepsChange(selectedReps)
                    userTriggeredAction += 1
                }
            }
            Box(modifier = Modifier.weight(.2F)) {
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalScrollSelector(pagerItemList = listOf("✕"))
                }
            }
            Box(modifier = Modifier.weight(.4F)) {
                Column(Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Berat (kg)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    HorizontalScrollSelector(
                        selectedItemIndex = initialWeightIndex,
                        pagerItemList = weightOptionList
                    ) {
                        selectedWeight = weightOptionList[it]
                        onWeightChange(selectedWeight)
                        userTriggeredAction += 1
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                onClick = {
                    addExerciseSetDone(selectedReps, selectedWeight)
                },
                shape = MaterialTheme.shapes.small,
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text(text = actionText)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HorizontalScrollSelector(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int = 0,
    pagerItemList: List<T>,
    onItemIndexSelected: (index: Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = selectedItemIndex) {
        pagerItemList.size
    }
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(7)
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                onItemIndexSelected(page)
            }
    }

    val coroutineScope = rememberCoroutineScope()

    Surface(modifier = modifier) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(112.dp)
                    .background(
                        Color.Black.copy(alpha = .25F)
                    )
            )

            VerticalPager(
                modifier = Modifier.height(280.dp),
                contentPadding = PaddingValues(vertical = 112.dp),
                state = pagerState,
                flingBehavior = fling
            ) { page ->

                Box(
                    modifier = Modifier
                        .bouncingClickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        }
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                            // We animate the alpha
                            alpha = lerp(
                                start = 0.25f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val textStyleByPage = if (pagerState.currentPage == page) {
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Text(
                        modifier = Modifier,
                        style = textStyleByPage,
                        text = pagerItemList[page].toString(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(112.dp)
                    .background(
                        Color.Black.copy(alpha = .25F)
                    )
            )
        }
    }
}