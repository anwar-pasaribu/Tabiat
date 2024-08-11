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
package features.inputWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.WorkoutPlan
import domain.repository.IGymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputWorkoutScreenViewModel(
    private val repository: IGymRepository,
) : ViewModel() {

    private val _workoutPlan = MutableStateFlow<WorkoutPlan?>(null)
    val workoutPlan = _workoutPlan.asStateFlow()

    fun getWorkoutPlan(workoutPlanId: Long) {
        viewModelScope.launch {
            _workoutPlan.value = repository.getWorkoutPlanById(workoutPlanId)
        }
    }

    fun createNewWorkoutPlan(workoutName: String, notes: String) {
        viewModelScope.launch {
            repository.createWorkoutPlan(workoutName, notes)
        }
    }

    fun saveWorkout(workoutPlanId: Long, workoutName: String, notes: String) {
        viewModelScope.launch {
            repository.updateWorkoutPlan(workoutPlanId, workoutName, notes)
        }
    }
}
