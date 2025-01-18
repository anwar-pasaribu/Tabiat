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
package features.workoutPlanDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.detail.DetailItemEntity
import domain.repository.IGymRepository
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import domain.usecase.GetWorkoutPlanByIdUseCase
import domain.usecase.detail.GetExerciseListWithProgressByWorkoutPlanUseCase
import domain.usecase.personalization.GetWorkoutPlanPersonalizationUseCase
import features.workoutPlanDetail.model.WorkoutPlanDetailUiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState()
    data class Success(val data: WorkoutPlanDetailUiData) : WorkoutDetailUiState()
    data class Error(val message: String) : WorkoutDetailUiState()
    data object NoExercise : WorkoutDetailUiState()
}

class WorkoutDetailScreenViewModel(
    private val repository: IGymRepository,
    private val getExerciseListByWorkoutPlanUseCase: GetExerciseListByWorkoutPlanUseCase,
    private val getWorkoutPlanByIdUseCase: GetWorkoutPlanByIdUseCase,
    private val getWoPersonalizationUseCase: GetWorkoutPlanPersonalizationUseCase,
    private val getExerciseListWithProgressByWorkoutPlanUseCase: GetExerciseListWithProgressByWorkoutPlanUseCase,
) : ViewModel() {

    private val _exerciseListStateFlow = MutableStateFlow(emptyList<DetailItemEntity>())
    val exerciseListStateFlow: StateFlow<List<DetailItemEntity>> =
        _exerciseListStateFlow.asStateFlow()

    private val _workoutPlanStateFlow =
        MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val workoutPlanStateFlow: StateFlow<WorkoutDetailUiState> = _workoutPlanStateFlow.asStateFlow()

    private fun loadWorkoutPlanExercises(workoutPlanId: Long) {
        viewModelScope.launch {
            getExerciseListWithProgressByWorkoutPlanUseCase.invoke(workoutPlanId)
                .collect { exerciseList ->
                    _exerciseListStateFlow.emit(exerciseList)
                }
        }
    }

    private fun loadWorkoutPlanById(workoutPlanId: Long) {
        viewModelScope.launch {
            val workoutPersonalization = getWoPersonalizationUseCase(workoutPlanId)
            val workoutPlan = getWorkoutPlanByIdUseCase(workoutPlanId)
            _workoutPlanStateFlow.update {
                WorkoutDetailUiState.Success(
                    data = WorkoutPlanDetailUiData(
                        workoutPlan = workoutPlan,
                        colorTheme = workoutPersonalization.colorTheme,
                    )
                )
            }
        }
    }

    fun deleteExercise(workoutPlanId: Long, workoutPlanExerciseId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
                workoutPlanId = workoutPlanId,
                exerciseId = workoutPlanExerciseId,
            )
        }
    }

    fun loadWorkoutPlanDetails(workoutPlanId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadWorkoutPlanExercises(workoutPlanId = workoutPlanId)
                loadWorkoutPlanById(workoutPlanId = workoutPlanId)
            }
        }
    }
}
