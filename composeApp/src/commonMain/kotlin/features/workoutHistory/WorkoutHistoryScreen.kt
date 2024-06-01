package features.workoutHistory

import PlayHapticAndSound
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import features.workoutHistory.model.MonthCalendarData
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import ui.component.BackButton

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun WorkoutHistoryScreen(
    onBack: () -> Unit = {},
) {

    val hazeState = remember { HazeState() }
    val selectedEmojiUnicode by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }

    val viewModel = koinInject<WorkoutHistoryScreenViewModel>()
    val historyUiState by viewModel.uiStateMutableStateFlow.collectAsState()

    if (selectedEmojiUnicode.isNotEmpty()) {
        PlayHapticAndSound(selectedEmojiUnicode)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCalenderData()
    }

    if (showSheet) {
        ExerciseLogListBottomSheet(
            targetDateTimeStamp = viewModel.selectedTimeStamp.collectAsState().value,
            onDismiss = {
                showSheet = false
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().hazeChild(
                    state = hazeState,
                    style = HazeMaterials.regular(MaterialTheme.colorScheme.background)
                ).background(Color.Transparent)
            ) {
                CenterAlignedTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                    navigationIcon = {
                        BackButton(
                            onClick = { onBack() },
                        )
                    },
                    title = {
                        Text(
                            "Riwayat Latihan",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        },
    ) { contentPadding ->

        val contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
            bottom = contentPadding.calculateBottomPadding() + WindowInsets.systemBars.asPaddingValues()
                .calculateBottomPadding()
        )

        AnimatedContent(
            targetState = historyUiState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300, delayMillis = 90))).togetherWith(fadeOut(animationSpec = tween(150)))
            }
        ) { uiState ->
            when (uiState) {
                WorkoutHistoryUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(contentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(40.dp))
                    }
                }

                is WorkoutHistoryUiState.Success -> {
                    CalendarContent(
                        modifier = Modifier.fillMaxSize().haze(state = hazeState),
                        calendarList = uiState.calendarItems,
                        contentPadding = contentPadding,
                        onCalendarDayClick = {
                            showSheet = true
                            viewModel.setSelectedDate(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarContent(
    modifier: Modifier = Modifier,
    calendarList: List<MonthCalendarData>,
    onCalendarDayClick: (LocalDate) -> Unit,
    contentPadding: PaddingValues
) {
    val lazyColumnListState = rememberLazyListState()

    LaunchedEffect(calendarList) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).month
        val itemPos = calendarList.indexOfFirst { it.month.month == today }
        lazyColumnListState.scrollToItem(itemPos)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyColumnListState,
        contentPadding = contentPadding,
    ) {

        items(
            items = calendarList,
            key = { it.month.monthNumber }
        ) { calendarItem ->
            WorkoutHistoryCalendarView(
                monthCalendarData = calendarItem,
                onClick = { selectedDate ->
                    onCalendarDayClick(selectedDate.day)
                },
            )
        }
    }
}