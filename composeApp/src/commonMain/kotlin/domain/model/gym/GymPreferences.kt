package domain.model.gym

import domain.enums.SoundEffectType

data class GymPreferences(
    val setTimerDuration: Int,
    val breakTimeDuration: Int,
    val timerSoundEffect: SoundEffectType
) {
    companion object {
        val DefaultGymPreferences = GymPreferences (
            0, 0, SoundEffectType.BOXING_BELL
        )
    }
}
