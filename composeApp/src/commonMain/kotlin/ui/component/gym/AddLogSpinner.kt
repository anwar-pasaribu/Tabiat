package ui.component.gym

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            Text(modifier = Modifier.align(Alignment.Center), text = "Add Log - " + selectedReps)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalScrollSelector(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int = 0,
    pagerItemList: List<Int>,
    onItemIndexSelected: (index: Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = selectedItemIndex) {
        pagerItemList.size
    }
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(5)
    )

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            onItemIndexSelected(page)
        }
    }
    Surface {
        Box(modifier = modifier.then(Modifier)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(56.dp)
                    .background(
                        Color.Black.copy(alpha = .25F)
                    )
            )

            VerticalPager(
                modifier = Modifier.height(168.dp),
                contentPadding = PaddingValues(vertical = 56.dp),
                state = pagerState,
                flingBehavior = fling
            ) { page ->

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                            // We animate the alpha, between 50% and 100%
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
                    .height(56.dp)
                    .background(
                        Color.Black.copy(alpha = .25F)
                    )
            )
        }
    }
}