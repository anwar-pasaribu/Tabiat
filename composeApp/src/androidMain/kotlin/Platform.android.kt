import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import domain.enums.PlatformType

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType
        get() = PlatformType.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun getScreenSizeInfo(): ScreenSizeInfo {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val hDp = config.screenHeightDp.dp
    val wDp = config.screenWidthDp.dp

    return remember(density, config) {
        ScreenSizeInfo(
            hPX = with(density) { hDp.roundToPx() },
            wPX = with(density) { wDp.roundToPx() },
            hDP = hDp,
            wDP = wDp
        )
    }
}


@Composable
actual fun PlayHapticAndSound(trigger: Any) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val vibrator = remember {
        ContextCompat.getSystemService(context, Vibrator::class.java)
    }
    val audioManager = remember {
        ContextCompat.getSystemService(context, AudioManager::class.java)
    }

    LaunchedEffect(trigger) {
        audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1f)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                50,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
}