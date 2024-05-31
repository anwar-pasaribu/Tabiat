package platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import domain.enums.SoundEffectType
import korlibs.audio.sound.await
import korlibs.audio.sound.readMusic
import korlibs.io.android.withAndroidContext
import korlibs.io.file.std.VfsFileFromData
import org.jetbrains.compose.resources.ExperimentalResourceApi
import tabiat.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun PlaySoundEffect(trigger: Any, soundEffectType: SoundEffectType) {
    if (soundEffectType == SoundEffectType.NONE) return
    val context = LocalContext.current
    LaunchedEffect(trigger) {
        withAndroidContext(context) {
            val soundPath = when(soundEffectType) {
                SoundEffectType.BOXING_BELL -> "files/boxing_bell_cut.mp3"
                SoundEffectType.FAIRY_MESSAGE -> "files/mixkit_fairy_message_notification_861.wav"
                SoundEffectType.UPLIFTING_BELL -> "files/mixkit_uplifting_bells_notification_938.wav"
                SoundEffectType.NONE -> ""
            }
            val soundExtension = when(soundEffectType) {
                SoundEffectType.BOXING_BELL -> "mp3"
                SoundEffectType.FAIRY_MESSAGE -> "wav"
                SoundEffectType.UPLIFTING_BELL -> "wav"
                SoundEffectType.NONE -> ""
            }
            val bytes = Res.readBytes(soundPath)
            val sound = VfsFileFromData(bytes, soundExtension).readMusic()
            val channel = sound.play()
            channel.await()
        }
    }
}