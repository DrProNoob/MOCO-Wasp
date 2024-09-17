package feed.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import core.model.repo.ChallengeEvent

@Composable
fun ChallengeDialog(
    challengeState: ChallengeState,
    onEvent: (ChallengeEvent) -> Unit
) {

    val openDialog = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onEvent(ChallengeEvent.HideDialog) },
        properties = DialogProperties(),
        content = { DialogContent(challengeState, onEvent) }
    )

}

@Composable
private fun DialogContent(challengeState: ChallengeState,onEvent: (ChallengeEvent) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            challengeState.challenge?.let { Text(text = it.challengeAction) }
            Button(
                onClick = { onEvent(ChallengeEvent.StartChallenge(challengeState.challenge!!)) },
            ) {
                Text(
                    text = "Start Challenge"

                )
            }
        }
    }
}