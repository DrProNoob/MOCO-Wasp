package steps.domain.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun StepScreen(viewModel: StepViewModel) {
    val count = viewModel.counterState.collectAsStateWithLifecycle()

    Text("Gemachte schritte: ${count.value}")
}