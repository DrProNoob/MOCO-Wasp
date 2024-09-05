package camera.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
expect fun MocoCamera(
    state: MocoCameraState,
    modifier: Modifier
)