import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.unwur.tabiatmu.MainActivity
import com.unwur.tabiatmu.R
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

    //val player = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
    //player.start()
    LaunchedEffect(trigger) {
        audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, -1f)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    50,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }
}

@Composable
actual fun SendNotification(title: String, body: String) {
    val context = LocalContext.current

    // Create a PendingIntent
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    // Create a notification channel for Android 8.0 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "YOUR_CHANNEL_ID",
            "YOUR_CHANNEL_NAME",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Build the notification
    val notificationBuilder = NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.notif_icon_32dp) // Replace with your icon resource
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    // Send the notification
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(0, notificationBuilder.build())

//    val player = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
//    player.start()
}