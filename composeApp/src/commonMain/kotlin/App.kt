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
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import features.createNewExercise.CreateExerciseScreen
import features.home.HomeScreen
import features.inputExercise.InputExerciseScreen
import features.inputWorkout.InputWorkoutScreen
import features.logWorkoutExercise.LogWorkoutExerciseScreen
import features.navigationHelper.NavigationViewModel
import features.settings.SettingsScreen
import features.workoutHistory.WorkoutHistoryScreen
import features.workoutPlanDetail.WorkoutDetailScreen
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.tabiat_icon_32dp
import ui.component.AddIconButton
import ui.component.BackButton
import ui.component.ImageWrapper
import ui.component.gym.FloatingTimerView
import ui.theme.MyAppTheme
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

sealed class MyAppRoute {
    @Serializable object Home
    @Serializable class InputWorkout(val workoutPlanId: Long = 0L)
    @Serializable class WorkoutDetail(val workoutPlanId: Long)
    @Serializable class InputExercise(val workoutPlanId: Long)
    @Serializable class LogWorkoutExercise(val currentWorkoutPlanId: Long, val currentExerciseId: Long)
    @Serializable object WorkoutHistory
    @Serializable object CreateNewExercise
    @Serializable object Settings
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class,
    ExperimentalHazeMaterialsApi::class
)
@Composable
@Preview
fun App(
    shouldDarkTheme: Boolean = isSystemInDarkTheme(),
    navController: NavHostController = rememberNavController(),
) {
    KoinContext {
        MyAppTheme(useDarkColors = shouldDarkTheme) {
            val navViewModel = koinInject<NavigationViewModel>()
            val homeScreenVisible = remember { mutableStateOf(true) }
            val logExerciseScreenVisible = remember { mutableStateOf(false) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            navBackStackEntry?.destination?.let { currentDestination ->
                homeScreenVisible.value = currentDestination.hasRoute(MyAppRoute.Home::class)
                logExerciseScreenVisible.value =
                    currentDestination.hasRoute(MyAppRoute.LogWorkoutExercise::class)
            }

            val hazeState = remember { HazeState() }

            Box(Modifier.fillMaxSize()) {
                SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this
                    ) {
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    modifier = Modifier.fillMaxWidth().hazeChild(
                                        state = hazeState,
                                        style = HazeMaterials.regular(MaterialTheme.colorScheme.background),
                                    ),
                                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                                    title = {
                                        if (homeScreenVisible.value) {
                                            ImageWrapper(
                                                resource = Res.drawable.tabiat_icon_32dp,
                                                contentDescription = "",
                                            )
                                        }
                                    },
                                    navigationIcon = {
                                        if (homeScreenVisible.value) {
                                            IconButton(onClick = {
                                                navController.navigate(
                                                    MyAppRoute.Settings
                                                )
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Settings,
                                                    contentDescription = "Setting Menu",
                                                )
                                            }
                                        } else {
                                            BackButton(
                                                showBackground = logExerciseScreenVisible.value
                                            ) {
                                                navController.navigateUp()
                                            }
                                        }
                                    },
                                    actions = {
                                        if (homeScreenVisible.value) {
                                            AddIconButton(
                                                onClick = {
                                                    navController.navigate(MyAppRoute.InputWorkout())
                                                }
                                            )
                                            Spacer(Modifier.width(8.dp))
                                        }
                                    },
                                )
                            },
                        ) { contentPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = MyAppRoute.Home,
                            ) {
                                composableWithCompositionLocal<MyAppRoute.Home> {
                                    HomeScreen(
                                        paddingValues = contentPadding,
                                        hazeState = hazeState,
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
                                composableWithCompositionLocal<MyAppRoute.WorkoutDetail> { backStackEntry ->
                                    val workoutDetail: MyAppRoute.WorkoutDetail =
                                        backStackEntry.toRoute()
                                    WorkoutDetailScreen(
                                        paddingValues = contentPadding,
                                        workoutPlanId = workoutDetail.workoutPlanId,
                                        onBack = {
                                            navController.navigateUp()
                                        },
                                        onNewExerciseToWorkoutPlan = {
                                            navController.navigate(
                                                MyAppRoute.InputExercise(
                                                    workoutDetail.workoutPlanId
                                                )
                                            )
                                        },
                                        onSelectExercise = {
                                            navController.navigate(
                                                MyAppRoute.LogWorkoutExercise(
                                                    workoutDetail.workoutPlanId,
                                                    it
                                                )
                                            )
                                        },
                                    )
                                }
                                composableWithCompositionLocal<MyAppRoute.InputWorkout> {
                                    val inputWorkout: MyAppRoute.InputWorkout = it.toRoute()
                                    InputWorkoutScreen(
                                        paddingValues = contentPadding,
                                        workoutPlanId = inputWorkout.workoutPlanId,
                                        onBack = {
                                            navController.navigateUp()
                                        },
                                        onWorkoutSaved = {
                                            navController.navigateUp()
                                        },
                                    )
                                }
                                composableWithCompositionLocal<MyAppRoute.InputExercise> {
                                    val inputExercise: MyAppRoute.InputExercise = it.toRoute()
                                    InputExerciseScreen(
                                        contentPadding = contentPadding,
                                        workoutPlanId = inputExercise.workoutPlanId,
                                        onBack = {
                                            navController.navigateUp()
                                        },
                                        onCreateNewExerciseRequested = {
                                            navController.navigate(MyAppRoute.CreateNewExercise)
                                        },
                                    )
                                }
                                composableWithCompositionLocal<MyAppRoute.LogWorkoutExercise> {
                                    val logWorkoutExercise: MyAppRoute.LogWorkoutExercise =
                                        it.toRoute()
                                    LogWorkoutExerciseScreen(
                                        contentPadding = contentPadding,
                                        workoutPlanId = logWorkoutExercise.currentWorkoutPlanId,
                                        exerciseId = logWorkoutExercise.currentExerciseId,
                                        onBack = {
                                            navController.navigateUp()
                                        },
                                    )
                                }
                                composableWithCompositionLocal<MyAppRoute.WorkoutHistory> {
                                    WorkoutHistoryScreen(
                                        contentPadding = contentPadding,
                                        hazeState = hazeState,
                                    ) {
                                        navController.navigateUp()
                                    }
                                }
                                composableWithCompositionLocal<MyAppRoute.CreateNewExercise> {
                                    CreateExerciseScreen(
                                        contentPadding = contentPadding,
                                        onBack = {
                                            navController.navigateUp()
                                        },
                                        onNewExerciseCreated = {
                                            navController.navigateUp()
                                        },
                                    )
                                }
                                composableWithCompositionLocal<MyAppRoute.Settings> {
                                    SettingsScreen(
                                        contentPadding = contentPadding
                                    )
                                }
                            }

                        }
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

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

val LocalHazeState = compositionLocalOf<HazeState?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
val tabiatDetailBoundsTransform = BoundsTransform { _, _ ->
    spatialExpressiveSpring()
}

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

inline fun <reified T : Any> NavGraphBuilder.composableWithCompositionLocal(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    noinline exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    noinline popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    noinline popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    crossinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        typeMap,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}
