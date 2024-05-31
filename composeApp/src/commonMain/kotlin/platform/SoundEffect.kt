package platform

import androidx.compose.runtime.Composable
import domain.enums.SoundEffectType

@Composable
expect fun PlaySoundEffect(trigger: Any, soundEffectType: SoundEffectType)