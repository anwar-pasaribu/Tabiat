import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import domain.enums.PlatformType

interface Platform {
    val name: String
    val type: PlatformType
}

expect fun getPlatform(): Platform

data class ScreenSizeInfo(val hPX: Int, val wPX: Int, val hDP: Dp, val wDP: Dp)

@Composable
expect fun getScreenSizeInfo(): ScreenSizeInfo

@Composable
expect fun PlayHapticAndSound(trigger: Any)

@Composable
expect fun SendNotification(title: String, body: String)
