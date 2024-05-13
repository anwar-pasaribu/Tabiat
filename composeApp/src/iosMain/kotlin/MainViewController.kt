import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController
import di.letsKoinStart
import platform.UIKit.UIViewController

@OptIn(ExperimentalComposeApi::class)
fun MainViewController(): UIViewController {
    letsKoinStart()
    return ComposeUIViewController(
        configure = {
            platformLayers = true
        }
    ) {
        App()
    }
}