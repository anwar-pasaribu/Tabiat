/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
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
            val soundPath = when (soundEffectType) {
                SoundEffectType.BOXING_BELL -> "files/boxing_bell_cut.mp3"
                SoundEffectType.FAIRY_MESSAGE -> "files/mixkit_fairy_message_notification_861.wav"
                SoundEffectType.UPLIFTING_BELL -> "files/mixkit_uplifting_bells_notification_938.wav"
                SoundEffectType.NONE -> ""
            }
            val soundExtension = when (soundEffectType) {
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
