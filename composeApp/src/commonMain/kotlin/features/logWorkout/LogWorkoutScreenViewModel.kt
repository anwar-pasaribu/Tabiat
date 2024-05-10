package features.logWorkout

import androidx.lifecycle.ViewModel
import domain.repository.IGymRepository

class LogWorkoutScreenViewModel(
    private val gymRepository: IGymRepository
) : ViewModel() {
}