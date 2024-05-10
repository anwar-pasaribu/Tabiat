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
import features.logWorkout.LogWorkoutScreen
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


enum class MyAppScreen(val title: StringResource) {
    Start(title = Res.string.title_home),
    LogWorkout(title = Res.string.title_detail),
    InputExercise(title = Res.string.title_input),
    WorkoutHistory(title = Res.string.title_workout_history),
}

@Composable
@Preview
fun App(
    shouldDarkTheme: Boolean = isSystemInDarkTheme(),
    navController: NavHostController = rememberNavController()
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
                            navController.navigate(MyAppScreen.LogWorkout.name)
                        },
                        openHistoryScreen = {
                            navController.navigate(MyAppScreen.WorkoutHistory.name)
                        }
                    )
                }
                composable(route = MyAppScreen.LogWorkout.name) {
                    LogWorkoutScreen {
                        navController.navigateUp()
                    }
                }
                composable(route = MyAppScreen.InputExercise.name) {
                    InputExerciseScreen {
                        navController.navigateUp()
                    }
                }
                composable(route = MyAppScreen.WorkoutHistory.name) {
                    WorkoutHistoryScreen {
                        navController.navigateUp()
                    }
                }
            }
        }
    }
}