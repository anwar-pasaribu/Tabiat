package ui.component.gym

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    onTimerFinished: () -> Unit,
    onCancelTimer: () -> Unit,
) {

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


    Box(
        modifier = modifier.then(Modifier.fillMaxSize()),
        contentAlignment = Alignment.Center
    ) {
        Column (modifier = Modifier.background(Color.Transparent)){

            IconButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                onClick = {
                    onCancelTimer()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = ""
                )
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1 / 1F)
                    .background(Color.Red, CircleShape),
            ) {

                Row(modifier = Modifier.align(Alignment.Center)) {
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
                                }, label = ""
                            ) { count ->
                                Text(
                                    modifier = Modifier,
                                    text = "${count.digitChar}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 128.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                }
            }
        }
    }
}