package features.workoutHistory

import androidx.lifecycle.ViewModel
import domain.repository.IGymRepository

class WorkoutHistoryScreenViewModel(
    private val gymRepository: IGymRepository
) : ViewModel() {
}