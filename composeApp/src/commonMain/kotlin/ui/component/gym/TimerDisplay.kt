package ui.component.gym

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
    onCancelTimer: () -> Unit,
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
                onClick = { onCancelTimer() }
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.size(324.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(324.dp)
                        .background(bigCircleBackground, CircleShape)
                        .border(4.dp, bigCircleBackground, CircleShape)
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
                                        if (targetState < initialState) {
                                            slideInVertically { -it } togetherWith slideOutVertically { it }
                                        } else {
                                            slideInVertically { it } togetherWith slideOutVertically { -it }
                                        }
                                    }, label = "timerAnimation"
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
                    targetValue = timeLeft/countDownInitial.toFloat(),
                    animationSpec = tween(),
                    label = "animateTimerProgress"
                )
                CircularProgressIndicator(
                    progress = {
                        animatedFloat.value
                    },
                    modifier = Modifier.size(size = 324.dp).align(Alignment.Center),
                    color = Color.Red,
                    strokeWidth = 4.dp,
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
        Text(btnText, style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold
        ))
    }
}