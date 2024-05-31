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
import features.createNewExercise.CreateExerciseScreen
import features.home.HomeScreen
import features.inputExercise.InputExerciseScreen
import features.inputWorkout.InputWorkoutScreen
import features.logWorkoutExercise.LogWorkoutExerciseScreen
import features.navigationHelper.NavigationViewModel
import features.workoutHistory.WorkoutHistoryScreen
import features.workoutPlanDetail.WorkoutDetailScreen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.title_create_new_exercise
import tabiat.composeapp.generated.resources.title_detail
import tabiat.composeapp.generated.resources.title_home
import tabiat.composeapp.generated.resources.title_input_exercise
import tabiat.composeapp.generated.resources.title_input_workout
import tabiat.composeapp.generated.resources.title_log_exercise
import tabiat.composeapp.generated.resources.title_workout_history
import ui.component.gym.FloatingTimerView
import ui.theme.MyAppTheme


enum class MyAppScreen(val title: StringResource) {
    Start(title = Res.string.title_home),
    InputWorkout(title = Res.string.title_input_workout),
    WorkoutDetail(title = Res.string.title_detail),
    InputExercise(title = Res.string.title_input_exercise),
    LogWorkoutExercise(title = Res.string.title_log_exercise),
    WorkoutHistory(title = Res.string.title_workout_history),
    CreateNewExercise(title = Res.string.title_create_new_exercise),
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
                    startDestination = MyAppScreen.Start.name,
                    enterTransition = {
                        fadeIn() + slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec
                        ) + fadeOut()
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec
                        ) + fadeIn()
                    },
                    popExitTransition = {
                        fadeOut() + slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec
                        )
                    }
                ) {
                    composable(route = MyAppScreen.Start.name) {
                        HomeScreen(
                            onWorkoutDetail = {
                                navViewModel.currentWorkoutPlanId.value = it
                                navController.navigate(
                                    route = MyAppScreen.WorkoutDetail.name,
                                )
                            },
                            onEditWorkout = {
                                navViewModel.currentWorkoutPlanId.value = it
                                navController.navigate(MyAppScreen.InputWorkout.name)
                            },
                            openHistoryScreen = {
                                navController.navigate(MyAppScreen.WorkoutHistory.name)
                            },
                            onCreateNewWorkoutPlan = {
                                navViewModel.currentWorkoutPlanId.value = 0L
                                navController.navigate(MyAppScreen.InputWorkout.name)
                            }
                        )
                    }
                    composable(route = MyAppScreen.InputWorkout.name) {
                        InputWorkoutScreen(
                            workoutPlanId = navViewModel.currentWorkoutPlanId.value,
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
                            }
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

                val currentTimer by navViewModel.currentTimerLeftDuration.collectAsState()
                val initialTimerDuration by navViewModel.initialTimerDuration.collectAsState()
                val initialBreakTimeDuration by navViewModel.initialBreakTimeDuration.collectAsState()
                val timerSoundEffect by navViewModel.timerSoundEffect.collectAsState()
                AnimatedVisibility(
                    visible = currentTimer != 0,
                    enter = scaleIn(),
                    exit = scaleOut(tween(4000)) + fadeOut(),
                    label = "animate_floating_timer"
                ) {
                    FloatingTimerView(
                        timerLeft = currentTimer,
                        initialDuration = initialTimerDuration,
                        initialBreakTimeDuration = initialBreakTimeDuration,
                        timerSoundEffect = timerSoundEffect
                    )
                }
            }
        }
    }
}