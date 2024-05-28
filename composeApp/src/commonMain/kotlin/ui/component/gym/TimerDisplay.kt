package ui.component.gym

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import getScreenSizeInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class Digit(val digitChar: Char, val fullNumber: Int, val place: Int) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Digit -> digitChar == other.digitChar
            else -> super.equals(other)
        }
    }
}

operator fun Digit.compareTo(other: Digit): Int {
    return fullNumber.compareTo(other.fullNumber)
}

@Composable
fun TimerDisplay(
    modifier: Modifier = Modifier,
    countDown: Int,
    breakTime: Boolean = false,
    onTimerFinished: () -> Unit,
    onCancelTimer: (Int) -> Unit,
) {

    var countDownInitial by remember {
        mutableIntStateOf(countDown)
    }

    var timeLeft by remember {
        mutableIntStateOf(countDown)
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                onTimerFinished()
            }
        }
    }

    val breakTimeBackground = if (breakTime) {
        MaterialTheme.colorScheme.surfaceDim
    } else {
        Color.Transparent
    }

    val bigCircleBackground = if (breakTime) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier.then(Modifier.fillMaxSize().background(breakTimeBackground)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            IconButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = { onCancelTimer(timeLeft) }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.size(324.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(324.dp)
                        .clip(CircleShape)
                        .background(bigCircleBackground, CircleShape)
                        .border(8.dp, bigCircleBackground, CircleShape)
                ) {
                    Row(modifier = Modifier.align(Alignment.Center)) {

                        val timerTextStyle = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 128.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        timeLeft.toString()
                            .mapIndexed { index, c -> Digit(c, timeLeft, index) }
                            .forEach { digit ->
                                AnimatedContent(
                                    targetState = digit,
                                    transitionSpec = {
                                        // Compare the incoming number with the previous number.
                                        if (targetState > initialState) {
                                            // If the target number is larger, it slides up and fades in
                                            // while the initial (smaller) number slides up and fades out.
                                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                                slideOutVertically { height -> -height } + fadeOut())
                                        } else {
                                            // If the target number is smaller, it slides down and fades in
                                            // while the initial number slides down and fades out.
                                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                                slideOutVertically { height -> height } + fadeOut())
                                        }.using(
                                            // Disable clipping since the faded slide-in/out should
                                            // be displayed out of bounds.
                                            SizeTransform(clip = false)
                                        )
                                    },
                                    label = "timerAnimation"
                                ) { count ->
                                    Text(
                                        modifier = Modifier,
                                        text = "${count.digitChar}",
                                        style = timerTextStyle
                                    )
                                }
                            }
                    }
                }
                val animatedFloat = animateFloatAsState(
                    targetValue = timeLeft / countDownInitial.toFloat(),
                    animationSpec = tween(),
                    label = "animateTimerProgress"
                )
                CircularProgressIndicator(
                    progress = {
                        animatedFloat.value
                    },
                    modifier = Modifier.size(size = 324.dp).align(Alignment.Center),
                    color = Color.Red,
                    strokeWidth = 8.dp,
                    trackColor = Color.White,
                    strokeCap = StrokeCap.Round,
                )
            }

            if (!breakTime) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    ExtendTimerButton(
                        btnText = "5",
                        onClick = {
                            timeLeft += 5
                            countDownInitial += 5
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    ExtendTimerButton(
                        btnText = "15",
                        onClick = {
                            timeLeft += 15
                            countDownInitial += 15
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimerDisplayFloating(
    modifier: Modifier = Modifier,
    countDown: Int,
    countDownInitial: Int,
    onTimerFinished: () -> Unit = {},
) {

    val bigCircleBackground = Color.Red

    Box(modifier = modifier.then(Modifier.size(100.dp)), Alignment.Center) {
        Card(
            modifier = modifier.then(Modifier.size(72.dp)),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(
                modifier = Modifier.clip(CircleShape).size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(bigCircleBackground, CircleShape)
                        .border(4.dp, bigCircleBackground, CircleShape)
                ) {
                    Row(modifier = Modifier.align(Alignment.Center)) {

                        val timerTextStyle = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        countDown.toString()
                            .mapIndexed { index, c -> Digit(c, countDown, index) }
                            .forEach { digit ->
                                AnimatedContent(
                                    targetState = digit,
                                    transitionSpec = {
                                        if (targetState > initialState) {
                                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                                slideOutVertically { height -> -height } + fadeOut())
                                        } else {
                                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                                slideOutVertically { height -> height } + fadeOut())
                                        }.using(
                                            SizeTransform(clip = false)
                                        )
                                    },
                                    label = "timerAnimation"
                                ) { count ->
                                    Text(
                                        modifier = Modifier,
                                        text = "${count.digitChar}",
                                        style = timerTextStyle
                                    )
                                }
                            }
                    }
                }
                val animatedFloat = animateFloatAsState(
                    targetValue = countDown / countDownInitial.toFloat(),
                    animationSpec = tween(),
                    label = "animateTimerProgress"
                )
                CircularProgressIndicator(
                    progress = {
                        animatedFloat.value
                    },
                    modifier = Modifier.size(size = 72.dp).align(Alignment.Center),
                    color = Color.Black,
                    strokeWidth = 5.dp,
                    trackColor = Color.Red,
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
fun FloatingTimerView(timerLeft: Int, initialDuration: Int, initialBreakTimeDuration: Int) {

    if (initialDuration == 0 || initialBreakTimeDuration == 0) return

    var timerDuration by remember { mutableIntStateOf(timerLeft) }

    val screenWidth = getScreenSizeInfo().wDP
    val screenHeight = getScreenSizeInfo().hDP
    val screenWidthPx = getScreenSizeInfo().wPX
    val boxSize = 100.dp
    val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }
    val boxOffsetX = remember { Animatable(((screenWidthPx / 2) - (boxSizePx / 2))) }
    val boxOffsetY = remember { Animatable(0F) }

    val coroutineScope = rememberCoroutineScope()
    var timerVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(timerLeft) {
        timerVisible = timerLeft > 0
        timerDuration = timerLeft
    }

    val scaleAnimVal by animateFloatAsState(
        targetValue = if (timerVisible) 1F else 0F
    )

    Box(
        modifier = Modifier
            .scale(scaleAnimVal)
            .padding(
                vertical = WindowInsets.safeGestures.asPaddingValues().calculateTopPadding()
            )
            .fillMaxSize()
    ) {
        Box(
            Modifier
                .size(boxSize)
                .offset {
                    IntOffset(
                        boxOffsetX.value.roundToInt(),
                        boxOffsetY.value.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // Snap to the nearest side horizontally
                            val targetOffsetX =
                                if (boxOffsetX.value < (screenWidthPx / 2) - (boxSizePx / 2)) 0f
                                else (screenWidth - boxSize).toPx()
                            coroutineScope.launch {
                                boxOffsetX.animateTo(targetOffsetX)
                            }

                            // Ensure box doesn't go out of the screen vertically
                            val targetOffsetY =
                                boxOffsetY.value.coerceIn(0f, (screenHeight - boxSize).toPx())
                            coroutineScope.launch {
                                boxOffsetY.animateTo(targetOffsetY)
                            }
                        }
                    ) { change, dragAmount ->

                        change.consume()

                        val newOffsetX = boxOffsetX.value + dragAmount.x
                        val newOffsetY = boxOffsetY.value + dragAmount.y

                        coroutineScope.launch {
                            // Ensure the box doesn't go out of the screen
                            boxOffsetX.snapTo(
                                newOffsetX.coerceIn(
                                    0f,
                                    (screenWidth - boxSize).toPx()
                                )
                            )
                            boxOffsetY.snapTo(
                                newOffsetY.coerceIn(
                                    0f,
                                    (screenHeight - boxSize).toPx()
                                )
                            )
                        }
                    }
                }
        ) {
            val countDownInitial =
                if (initialDuration < timerDuration) initialBreakTimeDuration
                else initialDuration
            TimerDisplayFloating(
                modifier = Modifier,
                countDown = timerDuration,
                countDownInitial = countDownInitial,
                onTimerFinished = {
                    timerDuration = 0
                },
            )
        }
    }
}

@Composable
fun ExtendTimerButton(
    modifier: Modifier = Modifier,
    btnText: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 24.dp, bottom = 8.dp),
        onClick = { onClick() }
    ) {
        Icon(
            modifier = Modifier,
            imageVector = Icons.Default.Add,
            contentDescription = ""
        )
        Spacer(Modifier.width(2.dp))
        Text(
            btnText, style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}