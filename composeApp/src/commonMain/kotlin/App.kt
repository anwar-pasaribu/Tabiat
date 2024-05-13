@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import features.home.HomeScreen
import features.inputExercise.InputExerciseScreen
import features.workoutPlanDetail.WorkoutDetailScreen
import features.inputWorkout.InputWorkoutScreen
import features.workoutHistory.WorkoutHistoryScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.title_detail
import tabiat.composeapp.generated.resources.title_home
import tabiat.composeapp.generated.resources.title_input
import tabiat.composeapp.generated.resources.title_workout_history
import ui.theme.MyAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import features.createNewExercise.CreateExerciseScreen
import features.logWorkoutExercise.LogWorkoutExerciseScreen
import features.navigationHelper.NavigationViewModel


enum class MyAppScreen(val title: StringResource) {
    Start(title = Res.string.title_home),
    InputWorkout(title = Res.string.title_detail),
    WorkoutDetail(title = Res.string.title_detail),
    InputExercise(title = Res.string.title_input),
    LogWorkoutExercise(title = Res.string.title_input),
    WorkoutHistory(title = Res.string.title_workout_history),
    CreateNewExercise(title = Res.string.title_workout_history),
}

@Composable
@Preview
fun App(
    shouldDarkTheme: Boolean = isSystemInDarkTheme(),
    navController: NavHostController = rememberNavController(),
    navViewModel: NavigationViewModel = viewModel { NavigationViewModel() }
) {
    KoinContext {
        MyAppTheme {
            NavHost(
                navController = navController,
                startDestination = MyAppScreen.Start.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = MyAppScreen.Start.name) {
                    HomeScreen(
                        onWorkoutDetail = {
                            navViewModel.currentWorkoutPlanId.value = it
                            navController.navigate(
                                route = MyAppScreen.WorkoutDetail.name,
                            )
                        },
                        openHistoryScreen = {
                            navController.navigate(MyAppScreen.WorkoutHistory.name)
                        },
                        onCreateNewWorkoutPlan = {
                            navController.navigate(MyAppScreen.InputWorkout.name)
                        }
                    )
                }
                composable(route = MyAppScreen.InputWorkout.name) {
                    InputWorkoutScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                        onWorkoutSaved = {
                            navController.navigateUp()
                        }
                    )
                }
                composable(route = MyAppScreen.InputExercise.name) {
                    InputExerciseScreen(
                        workoutPlanId = navViewModel.currentWorkoutPlanId.value,
                        onBack = {
                            navController.navigateUp()
                        },
                        onCreateNewExerciseRequested = {
                            navController.navigate(MyAppScreen.CreateNewExercise.name)
                        }
                    )
                }
                composable(
                    route = MyAppScreen.WorkoutDetail.name,
                ) {
                    WorkoutDetailScreen(
                        workoutPlanId = navViewModel.currentWorkoutPlanId.value,
                        onBack = {
                            navController.navigateUp()
                        },
                        onNewExerciseToWorkoutPlan = {
                            navController.navigate(MyAppScreen.InputExercise.name)
                        },
                        onSelectExercise = {
                            navViewModel.currentExerciseId.value = it
                            navController.navigate(MyAppScreen.LogWorkoutExercise.name)
                        }
                    )
                }
                composable(route = MyAppScreen.LogWorkoutExercise.name) {
                    LogWorkoutExerciseScreen(
                        workoutPlanId = navViewModel.currentWorkoutPlanId.value,
                        exerciseId = navViewModel.currentExerciseId.value,
                        onBack = {
                            navController.navigateUp()
                        },
                    )
                }
                composable(route = MyAppScreen.WorkoutHistory.name) {
                    WorkoutHistoryScreen {
                        navController.navigateUp()
                    }
                }
                composable(route = MyAppScreen.CreateNewExercise.name) {
                    CreateExerciseScreen(
                        onBack = {
                            navController.navigateUp()
                        },
                        onNewExerciseCreated = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}