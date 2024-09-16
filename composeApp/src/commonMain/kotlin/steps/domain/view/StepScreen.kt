package steps.domain.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun StepScreen(viewModel: StepViewModel) {
    var count by remember { mutableStateOf(0) }

    Text("Gemachte schritte: $count")
}