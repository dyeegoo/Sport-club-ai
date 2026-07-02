package com.sportclubai.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Message
import com.sportclubai.domain.usecase.GetClassMessagesUseCase
import com.sportclubai.domain.usecase.GetMessagesBetweenUsersUseCase
import com.sportclubai.domain.usecase.SendMessageUseCase
import com.sportclubai.domain.usecase.MarkMessageAsReadUseCase
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatState {
    object Loading : ChatState()
    data class Success(val messages: List<Message>) : ChatState()
    data class Error(val message: String) : ChatState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesBetweenUsersUseCase: GetMessagesBetweenUsersUseCase,
    private val getClassMessagesUseCase: GetClassMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val authRepository: AuthRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatState>(ChatState.Loading)
    val uiState = _uiState.asStateFlow()
    
    val currentUserId = MutableStateFlow("")
    val userRole = MutableStateFlow("")
    
    private var chatType: String = ""
    private var chatId: String = ""

    fun loadChat(type: String, id: String) {
        chatType = type
        chatId = id
        viewModelScope.launch {
            _uiState.value = ChatState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: ""
                currentUserId.value = uid
                val user = authRepository.getUser(uid)
                userRole.value = user?.role ?: ""
                
                val flow = when (type) {
                    "USER" -> getMessagesBetweenUsersUseCase(id)
                    "CLASS" -> getClassMessagesUseCase(id)
                    "ANNOUNCEMENT" -> {
                        messageRepository.getUserConversations(uid, uid)
                    }
                    else -> throw Exception("Invalid chat type")
                }
                
                flow.catch { e -> 
                    _uiState.value = ChatState.Error(e.message ?: "Failed to load chat")
                }.collect { messages ->
                    val filteredMessages = if (type == "ANNOUNCEMENT") {
                        messages.filter { it.messageType == "ANNOUNCEMENT" && it.classId.isEmpty() && it.receiverId.isEmpty() }.sortedBy { it.timestamp }
                    } else {
                        messages
                    }
                    
                    filteredMessages.filter { !it.readStatus && it.senderId != uid }.forEach {
                        markMessageAsReadUseCase(it.messageId)
                    }
                    
                    _uiState.value = ChatState.Success(filteredMessages)
                }
            } catch (e: Exception) {
                _uiState.value = ChatState.Error(e.message ?: "Failed to load chat")
            }
        }
    }
    
    fun sendMessage(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val message = Message(
                    receiverId = if (chatType == "USER") chatId else "",
                    classId = if (chatType == "CLASS") chatId else "",
                    messageType = if (chatType == "ANNOUNCEMENT") "ANNOUNCEMENT" else "TEXT",
                    content = content
                )
                sendMessageUseCase(message)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
