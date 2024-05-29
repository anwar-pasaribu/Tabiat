import androidx.compose.ui.window.ComposeUIViewController
import di.letsKoinStart
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    letsKoinStart()
    return ComposeUIViewController{
        App()
    }
}