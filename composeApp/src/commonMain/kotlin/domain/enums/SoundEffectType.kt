package domain.enums

enum class SoundEffectType(val code: Int) {
    NONE(0),
    BOXING_BELL(1),
    UPLIFTING_BELL(2),
    FAIRY_MESSAGE(3);

    companion object {
        fun fromCode(code: Int): SoundEffectType {
            return entries.find { it.code == code } ?: BOXING_BELL
        }
    }
}