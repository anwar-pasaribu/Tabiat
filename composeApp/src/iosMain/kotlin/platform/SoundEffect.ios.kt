package platform

import androidx.compose.runtime.Composable
import domain.enums.SoundEffectType
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun PlaySoundEffect(trigger: Any, soundEffectType: SoundEffectType) {

//    withAndroidContext(context) {
//        val soundPath = when(soundEffectType) {
//            SoundEffectType.BOXING_BELL -> "files/boxing_bell.mp3"
//            SoundEffectType.FAIRY_MESSAGE -> "files/mixkit_fairy_message_notification_861.wav"
//            SoundEffectType.UPLIFTING_BELL -> "files/mixkit_uplifting_bells_notification_938.wav"
//        }
//        val soundExtension = when(soundEffectType) {
//            SoundEffectType.BOXING_BELL -> "mp3"
//            SoundEffectType.FAIRY_MESSAGE -> "wav"
//            SoundEffectType.UPLIFTING_BELL -> "wav"
//        }
//        val bytes = Res.readBytes(soundPath)
//        val sound = VfsFileFromData(bytes, soundExtension).readMusic()
//        val channel = sound.play()
//        channel.await { current, total ->
//            println("$current/$total")
//        }
//    }

}