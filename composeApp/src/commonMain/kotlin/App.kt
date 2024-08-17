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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import features.createNewExercise.CreateExerciseScreen
import features.home.HomeScreen
import features.inputExercise.InputExerciseScreen
import features.inputWorkout.InputWorkoutScreen
import features.logWorkoutExercise.LogWorkoutExerciseScreen
import features.navigationHelper.NavigationViewModel
import features.workoutHistory.WorkoutHistoryScreen
import features.workoutPlanDetail.WorkoutDetailScreen
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import ui.component.gym.FloatingTimerView
import ui.theme.MyAppTheme

sealed class MyAppRoute {
    @Serializable object Home
    @Serializable class InputWorkout(val workoutPlanId: Long = 0L)
    @Serializable class WorkoutDetail(val workoutPlanId: Long)
    @Serializable class InputExercise(val workoutPlanId: Long)
    @Serializable class LogWorkoutExercise(val currentWorkoutPlanId: Long, val currentExerciseId: Long)
    @Serializable object WorkoutHistory
    @Serializable object CreateNewExercise
}

@Composable
@Preview
fun App(
    shouldDarkTheme: Boolean = isSystemInDarkTheme(),
    navController: NavHostController = rememberNavController(),
) {
    KoinContext {
        MyAppTheme(useDarkColors = shouldDarkTheme) {
            val navViewModel = koinInject<NavigationViewModel>()
            Box(Modifier.fillMaxSize()) {
                val animationSpec = tween<IntOffset>(300)
                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = MyAppRoute.Home,
                    enterTransition = {
                        fadeIn() + slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec,
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec,
                        ) + fadeOut()
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec,
                        ) + fadeIn()
                    },
                    popExitTransition = {
                        fadeOut() + slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec,
                        )
                    },
                ) {
                    composable<MyAppRoute.Home> {
                        HomeScreen(
                            onWorkoutDetail = {
                                navController.navigate(MyAppRoute.WorkoutDetail(it))
                            },
                            onEditWorkout = {
                                navController.navigate(MyAppRoute.InputWorkout(it))
                            },
                            openHistoryScreen = {
                                navController.navigate(MyAppRoute.WorkoutHistory)
                            },
                            onCreateNewWorkoutPlan = {
                                navController.navigate(MyAppRoute.InputWorkout())
                            },
                        )
                    }
                    composable<MyAppRoute.InputWorkout> {
                        val inputWorkout: MyAppRoute.InputWorkout = it.toRoute()
                        InputWorkoutScreen(
                            workoutPlanId = inputWorkout.workoutPlanId,
                            onBack = {
                                navController.navigateUp()
                            },
                            onWorkoutSaved = {
                                navController.navigateUp()
                            },
                        )
                    }
                    composable<MyAppRoute.InputExercise> {
                        val inputExercise: MyAppRoute.InputExercise = it.toRoute()
                        InputExerciseScreen(
                            workoutPlanId = inputExercise.workoutPlanId,
                            onBack = {
                                navController.navigateUp()
                            },
                            onCreateNewExerciseRequested = {
                                navController.navigate(MyAppRoute.CreateNewExercise)
                            },
                        )
                    }
                    composable<MyAppRoute.WorkoutDetail>{ backStackEntry ->
                        val workoutDetail: MyAppRoute.WorkoutDetail = backStackEntry.toRoute()
                        WorkoutDetailScreen(
                            workoutPlanId = workoutDetail.workoutPlanId,
                            onBack = {
                                navController.navigateUp()
                            },
                            onNewExerciseToWorkoutPlan = {
                                navController.navigate(MyAppRoute.InputExercise(workoutDetail.workoutPlanId))
                            },
                            onSelectExercise = {
                                navController.navigate(
                                    MyAppRoute.LogWorkoutExercise(workoutDetail.workoutPlanId, it)
                                )
                            },
                        )
                    }
                    composable<MyAppRoute.LogWorkoutExercise> {
                        val logWorkoutExercise: MyAppRoute.LogWorkoutExercise = it.toRoute()
                        LogWorkoutExerciseScreen(
                            workoutPlanId = logWorkoutExercise.currentWorkoutPlanId,
                            exerciseId = logWorkoutExercise.currentExerciseId,
                            onBack = {
                                navController.navigateUp()
                            },
                        )
                    }
                    composable<MyAppRoute.WorkoutHistory> {
                        WorkoutHistoryScreen {
                            navController.navigateUp()
                        }
                    }
                    composable<MyAppRoute.CreateNewExercise> {
                        CreateExerciseScreen(
                            onBack = {
                                navController.navigateUp()
                            },
                            onNewExerciseCreated = {
                                navController.navigateUp()
                            },
                        )
                    }
                }

                val currentTimer by navViewModel.currentTimerLeftDuration.collectAsState()
                val initialTimerDuration by navViewModel.initialTimerDuration.collectAsState()
                val initialBreakTimeDuration by navViewModel.initialBreakTimeDuration.collectAsState()
                val timerSoundEffect by navViewModel.timerSoundEffect.collectAsState()
                AnimatedVisibility(
                    visible = currentTimer != 0,
                    enter = scaleIn(),
                    exit = scaleOut(tween(4000)) + fadeOut(),
                    label = "animate_floating_timer",
                ) {
                    FloatingTimerView(
                        timerLeft = currentTimer,
                        initialDuration = initialTimerDuration,
                        initialBreakTimeDuration = initialBreakTimeDuration,
                        timerSoundEffect = timerSoundEffect,
                    )
                }
            }
        }
    }
}
