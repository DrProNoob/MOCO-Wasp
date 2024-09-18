package chat.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chat.model.ChatEvent
import chat.model.ChatRoom

@Composable
fun ChatScreen(viewModel:ChatViewModel) {
    var message by remember { mutableStateOf(TextFieldValue("")) }
    val messages by viewModel.messagesState.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent
    val chatRoom = viewModel.chatRoomState.collectAsStateWithLifecycle()
    val user = viewModel.user
    val navController = viewModel.navController

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECECEC))
            .padding(8.dp)
    ) {
        //onEvent(ChatEvent.SaveChatRoom(chatRoom = ChatRoom(1,2,2)))
        //onEvent(ChatEvent.GetChatRoom)
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
      //      Text("chatRoomId = ${chatRoom.value.chatRoom.ownUser} ")
            Button(
                onClick = { navController.popBackStack() }
            ){
                Text("Back")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages.messages) { message ->
                    ChatBubble(message.messageText, isUser = if(message.userId == user.value?.userId){
                        true
                    }else false)
                }
            }
        }

        // Message input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(12.dp),
                value = message,
                onValueChange = {message = it}
            )


            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    onEvent(ChatEvent.SaveMessage(messageText= message.text))
                    message = TextFieldValue("")
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF128C7E))
            ) {
                Icon(Icons.AutoMirrored.Outlined.Send,contentDescription = null)
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    Box(
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier
                .background(
                    color = if (isUser) Color(0xFFDCF8C6) else Color.White,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}
