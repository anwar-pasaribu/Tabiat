package features.exerciseDetail

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import org.koin.compose.koinInject
import ui.component.gym.ImagePager
import ui.extension.LocalNavAnimatedVisibilityScope
import ui.extension.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullImageViewerScreen(
    exerciseId: Long,
    imageUrlList: List<String>,
    onBack: () -> Unit,
) {

    val viewModel = koinInject<ExerciseDetailScreenViewModel>()

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    val exerciseName by viewModel.exerciseName.collectAsState()
    val exerciseImages by viewModel.exercisePics.collectAsState()

    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No sharedTransitionScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: LocalNavAnimatedContentScope.current

    with(sharedTransitionScope) {
        val imageUrl = remember { imageUrlList.getOrNull(0).orEmpty() }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ImagePager(
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "exercise-img-$imageUrl",
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        .fillMaxWidth(),
                    imageUrlList = exerciseImages,
                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}