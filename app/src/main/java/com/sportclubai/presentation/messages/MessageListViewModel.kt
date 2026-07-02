package com.sportclubai.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Message
import com.sportclubai.domain.usecase.GetUserConversationsUseCase
import com.sportclubai.domain.usecase.GetStudentsUseCase
import com.sportclubai.domain.usecase.GetCoachesUseCase
import com.sportclubai.domain.usecase.GetClassesUseCase
import com.sportclubai.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MessageListState {
    object Loading : MessageListState()
    data class Success(val conversations: List<ConversationItem>) : MessageListState()
    data class Error(val message: String) : MessageListState()
}

data class ConversationItem(
    val id: String,
    val type: String, // USER, CLASS, ANNOUNCEMENT
    val name: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val getUserConversationsUseCase: GetUserConversationsUseCase,
    private val getStudentsUseCase: GetStudentsUseCase,
    private val getCoachesUseCase: GetCoachesUseCase,
    private val getClassesUseCase: GetClassesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MessageListState>(MessageListState.Loading)
    val uiState = _uiState.asStateFlow()
    
    val currentUserId = MutableStateFlow("")
    val userRole = MutableStateFlow("")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = MessageListState.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: ""
                currentUserId.value = uid
                val user = authRepository.getUser(uid)
                userRole.value = user?.role ?: ""
                
                combine(
                    getUserConversationsUseCase(),
                    getStudentsUseCase(),
                    getCoachesUseCase(),
                    getClassesUseCase()
                ) { messages, students, coaches, classes ->
                    val userMap = mutableMapOf<String, String>()
                    students.forEach { userMap[it.studentId] = it.fullName }
                    coaches.forEach { userMap[it.coachId] = it.fullName }
                    
                    val classMap = classes.associateBy { it.classId }
                    
                    val conversations = mutableMapOf<String, ConversationItem>()
                    
                    for (message in messages) {
                        if (message.messageType == "ANNOUNCEMENT" && message.classId.isEmpty() && message.receiverId.isEmpty()) {
                            val id = "announcements"
                            if (!conversations.containsKey(id)) {
                                conversations[id] = ConversationItem(
                                    id = id,
                                    type = "ANNOUNCEMENT",
                                    name = "Club Announcements",
                                    lastMessage = message.content,
                                    lastMessageTime = message.timestamp,
                                    unreadCount = if (!message.readStatus && message.senderId != uid) 1 else 0
                                )
                            } else {
                                val current = conversations[id]!!
                                if (message.timestamp > current.lastMessageTime) {
                                    conversations[id] = current.copy(
                                        lastMessage = message.content,
                                        lastMessageTime = message.timestamp,
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                    )
                                } else {
                                     conversations[id] = current.copy(
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                     )
                                }
                            }
                        } else if (message.classId.isNotEmpty()) {
                            val id = message.classId
                            val className = classMap[id]?.className ?: "Class Group"
                            if (!conversations.containsKey(id)) {
                                conversations[id] = ConversationItem(
                                    id = id,
                                    type = "CLASS",
                                    name = className,
                                    lastMessage = message.content,
                                    lastMessageTime = message.timestamp,
                                    unreadCount = if (!message.readStatus && message.senderId != uid) 1 else 0
                                )
                            } else {
                                val current = conversations[id]!!
                                if (message.timestamp > current.lastMessageTime) {
                                    conversations[id] = current.copy(
                                        lastMessage = message.content,
                                        lastMessageTime = message.timestamp,
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                    )
                                } else {
                                    conversations[id] = current.copy(
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                     )
                                }
                            }
                        } else {
                            val id = if (message.senderId == uid) message.receiverId else message.senderId
                            if (id.isEmpty()) continue
                            val name = userMap[id] ?: "User"
                            if (!conversations.containsKey(id)) {
                                conversations[id] = ConversationItem(
                                    id = id,
                                    type = "USER",
                                    name = name,
                                    lastMessage = message.content,
                                    lastMessageTime = message.timestamp,
                                    unreadCount = if (!message.readStatus && message.senderId != uid) 1 else 0
                                )
                            } else {
                                val current = conversations[id]!!
                                if (message.timestamp > current.lastMessageTime) {
                                    conversations[id] = current.copy(
                                        lastMessage = message.content,
                                        lastMessageTime = message.timestamp,
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                    )
                                } else {
                                    conversations[id] = current.copy(
                                        unreadCount = current.unreadCount + if (!message.readStatus && message.senderId != uid) 1 else 0
                                     )
                                }
                            }
                        }
                    }
                    MessageListState.Success(conversations.values.sortedByDescending { it.lastMessageTime })
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = MessageListState.Error(e.message ?: "Failed to load messages")
            }
        }
    }
}
