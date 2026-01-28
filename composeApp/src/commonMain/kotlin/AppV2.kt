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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import features.createNewExercise.CreateExerciseScreen
import features.exerciseDetail.FullImageViewerScreen
import features.home.HomeScreen
import features.inputExercise.InputExerciseScreen
import features.inputWorkout.InputWorkoutScreen
import features.logWorkoutExercise.LogWorkoutExerciseScreen
import features.navigationHelper.NavigationViewModel
import features.settings.SettingsScreen
import features.workoutHistory.WorkoutHistoryScreen
import features.workoutPlanDetail.WorkoutDetailScreen
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import tabiat.composeapp.generated.resources.Res
import tabiat.composeapp.generated.resources.settings_24px
import tabiat.composeapp.generated.resources.tabiat_icon_32dp
import ui.component.AddIconButton
import ui.component.BackButton
import ui.component.ImageWrapper
import ui.component.gym.FloatingTimerView
import ui.extension.LocalSharedTransitionScope
import ui.theme.MyAppTheme


object MyAppRouteV2 {

    @Serializable open class Screen : NavKey
    @Serializable object Home : Screen()
    @Serializable data class InputWorkout(val workoutPlanId: Long = 0L) : Screen()
    @Serializable data class WorkoutDetail(
        val workoutPlanId: Long,
        val targetColorTheme: String = ""
    ) : Screen()
    @Serializable data class InputExercise(val workoutPlanId: Long) : Screen()
    @Serializable data class LogWorkoutExercise(
        val currentWorkoutPlanId: Long,
        val currentExerciseId: Long
    ) : Screen()
    @Serializable data object WorkoutHistory : Screen()
    @Serializable data object CreateNewExercise : Screen()
    @Serializable data object Settings : Screen()
    @Serializable data class FullImageViewer(
        val exerciseId: Long,
        val imageUrl: String
    ) : Screen()
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppV2(
    shouldDarkTheme: Boolean = isSystemInDarkTheme(),
) {
    KoinContext {
        MyAppTheme(useDarkColors = shouldDarkTheme) {
            val navViewModel = koinInject<NavigationViewModel>()
            val hazeState = remember { HazeState() }

            val backStack = remember { mutableStateListOf<NavKey>(MyAppRouteV2.Home) }

            val windowAdaptiveInfo = currentWindowAdaptiveInfo()
            val directive = remember(windowAdaptiveInfo) {
                calculatePaneScaffoldDirective(windowAdaptiveInfo)
                    .copy(horizontalPartitionSpacerSize = 0.dp)
            }
            val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

            Box(Modifier.fillMaxSize()) {
                Scaffold(
                    topBar = {
                        AppTopBarV2(
                            hazeState = hazeState,
                            onNavigateBack = { backStack.removeLastOrNull() },
                            backStack = backStack,
                            navigateTo = {
                                backStack.add(it)
                            }
                        )
                    },
                ) { contentPadding ->
                    SharedTransitionLayout {
                        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                            NavDisplay(
                                backStack = backStack,
                                onBack = { backStack.removeLastOrNull() },
                                sceneStrategy = listDetailStrategy,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                transitionSpec = {
                                    // New screen slides in from the right
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween()
                                    ) togetherWith
                                            // Current screen slides out to the left, but only partially to create a parallax effect
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                                                animationSpec = tween()
                                            )
                                },

                                // Defines the animation for navigating BACK (popping the stack)
                                popTransitionSpec = {
                                    // Screen being brought back slides in from its partial-left position
                                    slideInHorizontally(
                                        initialOffsetX = { fullWidth -> -fullWidth / 4 },
                                        animationSpec = tween()
                                    ) togetherWith
                                            // Screen being removed slides out to the right
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> fullWidth },
                                                animationSpec = tween()
                                            )
                                },

                                // Defines the animation for the interactive predictive back gesture
                                predictivePopTransitionSpec = {
                                    // This lambda provides access to the swipe event details
                                    // We will define a standard slide-out for simplicity, which works well with the gesture
                                    fadeIn() togetherWith slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween()
                                    )
                                },
                                entryProvider = entryProvider {
                                    entry<MyAppRouteV2.Home>(
                                        metadata = ListDetailSceneStrategy.listPane(
                                            detailPlaceholder = {
                                                Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                                                    Text("Choose a item from the list")
                                                }
                                            }
                                        ) + NavDisplay.transitionSpec {
                                                fadeIn() togetherWith fadeOut()
                                            } + NavDisplay.popTransitionSpec {
                                                fadeIn() togetherWith fadeOut()
                                            } + NavDisplay.predictivePopTransitionSpec {
                                                fadeIn() togetherWith fadeOut()
                                            }
                                    ) {
                                        HomeScreen(
                                            paddingValues = contentPadding,
                                            hazeState = hazeState,
                                            onWorkoutDetail = {
                                                backStack.add(
                                                    MyAppRouteV2.WorkoutDetail(
                                                        workoutPlanId = it.workoutPlanId,
                                                        targetColorTheme = it.rawColorTheme
                                                    )
                                                )
                                            },
                                            onEditWorkout = {
                                                backStack.add(MyAppRouteV2.InputWorkout(it))
                                            },
                                            openHistoryScreen = {
                                                backStack.add(MyAppRouteV2.WorkoutHistory)
                                            },
                                            onCreateNewWorkoutPlan = {
                                                backStack.add(MyAppRouteV2.InputWorkout())
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.WorkoutDetail>(
                                        metadata = ListDetailSceneStrategy.detailPane()
                                    ) { workoutDetail ->
                                        WorkoutDetailScreen(
                                            paddingValues = contentPadding,
                                            workoutPlanId = workoutDetail.workoutPlanId,
                                            targetColorTheme = workoutDetail.targetColorTheme,
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                            onNewExerciseToWorkoutPlan = {
                                                backStack.add(
                                                    MyAppRouteV2.InputExercise(
                                                        workoutDetail.workoutPlanId
                                                    )
                                                )
                                            },
                                            onSelectExercise = {
                                                backStack.add(
                                                    MyAppRouteV2.LogWorkoutExercise(
                                                        workoutDetail.workoutPlanId,
                                                        it
                                                    )
                                                )
                                            },
                                            onImageClick = { exerciseId, imageUrl ->
                                                backStack.add(
                                                    MyAppRouteV2.FullImageViewer(
                                                        exerciseId = exerciseId,
                                                        imageUrl = imageUrl ?: ""
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    entry<MyAppRouteV2.FullImageViewer>(
                                        metadata = NavDisplay.transitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        } + NavDisplay.popTransitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        } + NavDisplay.predictivePopTransitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        }
                                    ) { imageViewer ->
                                        FullImageViewerScreen(
                                            exerciseId = imageViewer.exerciseId,
                                            imageUrlList = listOf(imageViewer.imageUrl),
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.InputWorkout>(
                                        metadata = NavDisplay.transitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        } + NavDisplay.popTransitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        } + NavDisplay.predictivePopTransitionSpec {
                                            fadeIn() togetherWith fadeOut()
                                        }
                                    ) { inputWorkout ->
                                        InputWorkoutScreen(
                                            paddingValues = contentPadding,
                                            workoutPlanId = inputWorkout.workoutPlanId,
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                            onWorkoutSaved = {
                                                backStack.removeLastOrNull()
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.InputExercise> { inputExercise ->
                                        InputExerciseScreen(
                                            contentPadding = contentPadding,
                                            workoutPlanId = inputExercise.workoutPlanId,
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                            onCreateNewExerciseRequested = {
                                                backStack.add(MyAppRouteV2.CreateNewExercise)
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.LogWorkoutExercise> { logWorkoutExercise ->
                                        LogWorkoutExerciseScreen(
                                            contentPadding = contentPadding,
                                            workoutPlanId = logWorkoutExercise.currentWorkoutPlanId,
                                            exerciseId = logWorkoutExercise.currentExerciseId,
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.WorkoutHistory> {
                                        WorkoutHistoryScreen(
                                            contentPadding = contentPadding,
                                        )
                                    }
                                    entry<MyAppRouteV2.CreateNewExercise> {
                                        CreateExerciseScreen(
                                            contentPadding = contentPadding,
                                            onBack = {
                                                backStack.removeLastOrNull()
                                            },
                                            onNewExerciseCreated = {
                                                backStack.removeLastOrNull()
                                            },
                                        )
                                    }
                                    entry<MyAppRouteV2.Settings>(
                                        metadata = ListDetailSceneStrategy.extraPane()
                                    ) {
                                        SettingsScreen(
                                            contentPadding = contentPadding
                                        )
                                    }
                                }
                            )

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

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AppTopBarV2(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    backStack: List<NavKey>,
    onNavigateBack: () -> Unit = {},
    navigateTo: (MyAppRouteV2.Screen) -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth().hazeChild(
            state = hazeState,
            style = HazeMaterials.regular(MaterialTheme.colorScheme.background),
        ) {
            alpha = 0F
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
        title = {
            val isHomeScreen = backStack.size == 1 && backStack.firstOrNull() is MyAppRouteV2.Home
            if (isHomeScreen) {
                ImageWrapper(
                    resource = Res.drawable.tabiat_icon_32dp,
                    contentDescription = "Tabiat App Icon",
                )
            }
        },
        navigationIcon = {
            val isHomeScreen = backStack.size == 1 && backStack.firstOrNull() is MyAppRouteV2.Home

            AnimatedContent(
                targetState = isHomeScreen,
                label = "navigation_icon_animation",
                transitionSpec = {
                    // Define the bounce animation
                    scaleIn(initialScale = 0.8f)+fadeIn() togetherWith fadeOut() + scaleOut(targetScale = 0.8f)
                }
            ) { screenIsHome ->
                if (screenIsHome) {
                    IconButton(onClick = {
                        navigateTo.invoke(MyAppRouteV2.Settings)
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.settings_24px),
                            contentDescription = "Setting Menu",
                        )
                    }
                } else {
                    val isLogExerciseScreen = backStack.lastOrNull() is MyAppRouteV2.LogWorkoutExercise
                    val isMediaViewerScreen = backStack.lastOrNull() is MyAppRouteV2.FullImageViewer
                    BackButton(
                        showBackground = isLogExerciseScreen || isMediaViewerScreen
                    ) {
                        onNavigateBack()
                    }
                }
            }

        },
        actions = {
            val isHomeScreen = backStack.size == 1 && backStack.firstOrNull() is MyAppRouteV2.Home
            if (isHomeScreen) {
                AddIconButton(
                    onClick = {
                        navigateTo.invoke(MyAppRouteV2.InputWorkout())
                    }
                )
                Spacer(Modifier.width(8.dp))
            }
        },
    )
}
