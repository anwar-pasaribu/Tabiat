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
package features.createNewExercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.gym.Exercise
import domain.usecase.CreateNewExerciseUseCase
import domain.usecase.GetListExerciseCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateExerciseScreenViewModel(
    private val createNewExerciseUseCase: CreateNewExerciseUseCase,
    private val getListExerciseCategoryUseCase: GetListExerciseCategoryUseCase,
) : ViewModel() {

    private val _exerciseMuscleTargetList = MutableStateFlow<List<String>>(emptyList())
    val exerciseMuscleTargetList: StateFlow<List<String>> = _exerciseMuscleTargetList.asStateFlow()

    fun saveExercise(exercise: Exercise) {
        viewModelScope.launch {
            createNewExerciseUseCase.invoke(
                exercise.copy(
                    targetMuscle = getKeyByValue(exercise.targetMuscle),
                ),
            )
        }
    }

    fun loadMuscleGroupList() {
        viewModelScope.launch {
            _exerciseMuscleTargetList.update {
                getListExerciseCategoryUseCase.invoke().map { it.toUiDisplay() }
            }
        }
    }

    private fun String.toUiDisplay(): String {
        return muscleMap.getOrElse(this) { "-" }
    }

    private val muscleMap: Map<String, String> = mapOf(
        "quadriceps" to "quadrisep (paha)",
        "shoulders" to "bahu",
        "abdominals" to "perut",
        "chest" to "dada",
        "hamstrings" to "hamstring (paha blk)",
        "triceps" to "trisep (lengan bawah)",
        "biceps" to "bisep (lengan atas)",
        "lats" to "latissimus (punggung)",
        "middle back" to "punggung tengah",
        "calves" to "betis",
        "lower back" to "punggung bawah",
        "forearms" to "lengan bawah",
        "glutes" to "bokong",
        "traps" to "trapezius (punggung)",
        "adductors" to "adduktor (paha dlm)",
        "abductors" to "abduktor (paha luar)",
        "neck" to "leher",
    )

    private fun getKeyByValue(value: String): String {
        for ((key, mapValue) in muscleMap.entries) {
            if (mapValue == value) {
                return key
            }
        }
        return ""
    }
}
