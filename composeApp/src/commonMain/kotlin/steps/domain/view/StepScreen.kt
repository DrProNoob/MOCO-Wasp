package steps.domain.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import steps.domain.model.StepEvent


@Composable
fun StepScreen(viewModel: StepViewModel) {
    val count = viewModel.counterState.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent

    Column {
        Button(onClick = { onEvent(StepEvent.StartCounting) }) {
            Text("Starte zu zählen")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { onEvent(StepEvent.StopCounting) }) {
            Text("Stoppe zu zählen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Gemachte schritte: ${count.value}")
        Text("Ziel: 3000")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onEvent(StepEvent.StepsCounted) }) {
            Text("Lade Schritte")
        }
    }
}