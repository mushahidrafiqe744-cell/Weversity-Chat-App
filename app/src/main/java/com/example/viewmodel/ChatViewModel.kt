package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatUiState(
  val currentScreen: Screen = Screen.LANDING,
  val selectedTab: DashboardTab = DashboardTab.CHATS,
  val currentUser: User = SampleData.currentUser,
  val contacts: List<User> = SampleData.sampleContacts,
  val conversations: List<Conversation> = SampleData.initialConversations,
  val activeConversationId: String? = null,
  val messages: Map<String, List<Message>> = emptyMap(),
  val searchQuery: String = "",
  val isSearchOpen: Boolean = false,
  val isDarkMode: Boolean = false,
  val wallpaper: ChatWallpaper = ChatWallpaper.CLASSIC_DOODLE,
  val replyingTo: Message? = null,
  val isRecordingAudio: Boolean = false,
  val recordingDurationSec: Int = 0,
  val readReceiptsEnabled: Boolean = true,
  val notificationsEnabled: Boolean = true,
  val vibrationEnabled: Boolean = true,
  val lastSeenPrivacy: String = "Everyone" // Everyone, Contacts, Nobody
)

class ChatViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(ChatUiState())
  val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

  private val screenHistory = mutableListOf<Screen>()

  init {
    // Populate initial messages map
    val initialMsgMap = mutableMapOf<String, List<Message>>()
    SampleData.initialConversations.forEach { conv ->
      initialMsgMap[conv.id] = SampleData.getInitialMessages(conv.id)
    }
    _uiState.update { it.copy(messages = initialMsgMap) }
  }

  fun navigateTo(screen: Screen) {
    screenHistory.add(_uiState.value.currentScreen)
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun navigateBack(): Boolean {
    if (screenHistory.isNotEmpty()) {
      val prev = screenHistory.removeAt(screenHistory.size - 1)
      _uiState.update {
        it.copy(
          currentScreen = prev,
          replyingTo = null,
          isSearchOpen = false,
          searchQuery = ""
        )
      }
      return true
    } else if (_uiState.value.currentScreen != Screen.DASHBOARD && _uiState.value.currentScreen != Screen.LANDING) {
      _uiState.update { it.copy(currentScreen = Screen.DASHBOARD) }
      return true
    }
    return false
  }

  fun setTab(tab: DashboardTab) {
    _uiState.update { it.copy(selectedTab = tab) }
  }

  fun toggleSearch() {
    _uiState.update { it.copy(isSearchOpen = !it.isSearchOpen, searchQuery = "") }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun toggleDarkMode() {
    _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
  }

  fun setWallpaper(wallpaper: ChatWallpaper) {
    _uiState.update { it.copy(wallpaper = wallpaper) }
  }

  fun openConversation(conversationId: String) {
    _uiState.update {
      // Clear unread count for this conversation
      val updatedConvs = it.conversations.map { conv ->
        if (conv.id == conversationId) conv.copy(unreadCount = 0) else conv
      }
      it.copy(
        activeConversationId = conversationId,
        conversations = updatedConvs,
        replyingTo = null
      )
    }
    navigateTo(Screen.CHAT)
  }

  fun startChatWithContact(contact: User) {
    // Check if conversation already exists
    val existing = _uiState.value.conversations.find {
      !it.isGroup && it.participantIds.contains(contact.id)
    }
    if (existing != null) {
      openConversation(existing.id)
    } else {
      val newConv = Conversation(
        id = "conv_${contact.id}_${System.currentTimeMillis()}",
        isGroup = false,
        name = contact.name,
        participantIds = listOf(contact.id),
        participantNames = listOf(contact.name),
        lastMessageText = "Started new chat",
        lastMessageTime = "Just now",
        lastMessageStatus = MessageStatus.READ,
        unreadCount = 0,
        isOnline = contact.isOnline,
        description = contact.statusBio
      )
      val newMsgList = listOf(
        Message(
          id = "msg_welcome_${System.currentTimeMillis()}",
          conversationId = newConv.id,
          senderId = contact.id,
          senderName = contact.name,
          text = "Hey there! Thanks for reaching out on MyChat 👋",
          timestamp = formatCurrentTime(),
          status = MessageStatus.READ
        )
      )
      _uiState.update {
        it.copy(
          conversations = listOf(newConv) + it.conversations,
          messages = it.messages + (newConv.id to newMsgList),
          activeConversationId = newConv.id
        )
      }
      navigateTo(Screen.CHAT)
    }
  }

  fun setReplyingTo(message: Message?) {
    _uiState.update { it.copy(replyingTo = message) }
  }

  fun sendMessage(
    text: String,
    type: MessageType = MessageType.TEXT,
    mediaUrl: String? = null,
    mediaCaption: String? = null,
    audioDurationSec: Int = 0,
    documentName: String? = null,
    documentSize: String? = null
  ) {
    val convId = _uiState.value.activeConversationId ?: return
    val currentUser = _uiState.value.currentUser
    val replyTo = _uiState.value.replyingTo
    val newMsgId = "msg_${System.currentTimeMillis()}"
    val timeStr = formatCurrentTime()

    val newMsg = Message(
      id = newMsgId,
      conversationId = convId,
      senderId = currentUser.id,
      senderName = currentUser.name,
      text = text,
      timestamp = timeStr,
      status = MessageStatus.SENDING,
      type = type,
      mediaUrl = mediaUrl,
      mediaCaption = mediaCaption,
      audioDurationSec = audioDurationSec,
      documentName = documentName,
      documentSize = documentSize,
      replyTo = replyTo
    )

    // Update message map and conversation last message
    val currentList = _uiState.value.messages[convId] ?: emptyList()
    val updatedMessages = currentList + newMsg
    val previewText = when (type) {
      MessageType.TEXT -> text
      MessageType.AUDIO -> "🎤 Voice message (${audioDurationSec}s)"
      MessageType.IMAGE -> "📷 Photo"
      MessageType.DOCUMENT -> "📄 ${documentName ?: "Document"}"
    }

    val updatedConvs = _uiState.value.conversations.map { conv ->
      if (conv.id == convId) {
        conv.copy(
          lastMessageText = previewText,
          lastMessageTime = timeStr,
          lastMessageStatus = MessageStatus.SENDING
        )
      } else conv
    }

    _uiState.update {
      it.copy(
        messages = it.messages + (convId to updatedMessages),
        conversations = updatedConvs,
        replyingTo = null
      )
    }

    // Coroutine lifecycle to update delivery ticks and generate real-time reply
    viewModelScope.launch {
      delay(350)
      updateMessageStatus(convId, newMsgId, MessageStatus.SENT)

      delay(550)
      updateMessageStatus(convId, newMsgId, MessageStatus.DELIVERED)

      delay(800)
      updateMessageStatus(convId, newMsgId, MessageStatus.READ)

      // Auto-reply simulation for interactive feedback
      delay(1200)
      simulateContactResponse(convId, text)
    }
  }

  private fun updateMessageStatus(convId: String, msgId: String, status: MessageStatus) {
    _uiState.update { state ->
      val currentList = state.messages[convId] ?: return@update state
      val updated = currentList.map {
        if (it.id == msgId) it.copy(status = status) else it
      }
      val updatedConvs = state.conversations.map { conv ->
        if (conv.id == convId) conv.copy(lastMessageStatus = status) else conv
      }
      state.copy(
        messages = state.messages + (convId to updated),
        conversations = updatedConvs
      )
    }
  }

  private suspend fun simulateContactResponse(convId: String, userPrompt: String) {
    val conv = _uiState.value.conversations.find { it.id == convId } ?: return

    // Show typing in conversation
    _uiState.update { state ->
      val updatedConvs = state.conversations.map {
        if (it.id == convId) it.copy(typing = true) else it
      }
      state.copy(conversations = updatedConvs)
    }

    delay(1500)

    val partnerName = if (conv.isGroup) {
      conv.participantNames.randomOrNull() ?: "Member"
    } else {
      conv.name
    }

    val replyText = generateContextualReply(userPrompt, partnerName)
    val replyTime = formatCurrentTime()
    val replyMsg = Message(
      id = "reply_${System.currentTimeMillis()}",
      conversationId = convId,
      senderId = "partner_${convId}",
      senderName = partnerName,
      text = replyText,
      timestamp = replyTime,
      status = MessageStatus.READ
    )

    _uiState.update { state ->
      val currentList = state.messages[convId] ?: emptyList()
      val updatedConvs = state.conversations.map {
        if (it.id == convId) {
          it.copy(
            typing = false,
            lastMessageText = if (conv.isGroup) "$partnerName: $replyText" else replyText,
            lastMessageTime = replyTime,
            lastMessageStatus = MessageStatus.READ
          )
        } else it
      }
      state.copy(
        messages = state.messages + (convId to (currentList + replyMsg)),
        conversations = updatedConvs
      )
    }
  }

  private fun generateContextualReply(prompt: String, name: String): String {
    val lower = prompt.lowercase()
    return when {
      lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
        "Hey! Great to hear from you 😊 How is everything going?"
      lower.contains("how are you") ->
        "I'm doing fantastic, working on MyChat real-time messaging! How about you?"
      lower.contains("voice") || lower.contains("audio") ->
        "Loud and clear! The audio waveform recorder works flawlessly 🎙️"
      lower.contains("photo") || lower.contains("image") || lower.contains("picture") ->
        "Nice image! The preview looks very sharp."
      lower.contains("meeting") || lower.contains("call") ->
        "Sounds good! Let's jump on a quick call soon."
      lower.contains("deploy") || lower.contains("socket") || lower.contains("server") ->
        "The Socket.IO backend cluster is running smoothly with low ping."
      else ->
        "Got your message: \"$prompt\" 👍 Real-time delivery is lightning fast!"
    }
  }

  fun deleteMessage(convId: String, messageId: String) {
    _uiState.update { state ->
      val currentList = state.messages[convId] ?: return@update state
      val updated = currentList.map {
        if (it.id == messageId) it.copy(isDeleted = true, text = "This message was deleted") else it
      }
      state.copy(messages = state.messages + (convId to updated))
    }
  }

  // Voice recording simulation
  fun startVoiceRecording() {
    _uiState.update { it.copy(isRecordingAudio = true, recordingDurationSec = 0) }
    viewModelScope.launch {
      while (_uiState.value.isRecordingAudio) {
        delay(1000)
        if (_uiState.value.isRecordingAudio) {
          _uiState.update { it.copy(recordingDurationSec = it.recordingDurationSec + 1) }
        }
      }
    }
  }

  fun cancelVoiceRecording() {
    _uiState.update { it.copy(isRecordingAudio = false, recordingDurationSec = 0) }
  }

  fun finishAndSendVoiceRecording() {
    val duration = _uiState.value.recordingDurationSec.coerceAtLeast(1)
    _uiState.update { it.copy(isRecordingAudio = false, recordingDurationSec = 0) }
    sendMessage(
      text = "Voice message",
      type = MessageType.AUDIO,
      audioDurationSec = duration
    )
  }

  // New Group Creation
  fun createNewGroup(groupName: String, selectedUserIds: List<String>) {
    val selectedUsers = _uiState.value.contacts.filter { selectedUserIds.contains(it.id) }
    val newGroup = Conversation(
      id = "conv_group_${System.currentTimeMillis()}",
      isGroup = true,
      name = groupName,
      participantIds = selectedUserIds,
      participantNames = selectedUsers.map { it.name },
      lastMessageText = "You created group \"$groupName\"",
      lastMessageTime = "Just now",
      lastMessageStatus = MessageStatus.READ,
      unreadCount = 0,
      description = "Created on ${formatCurrentDate()}"
    )

    val introMsg = Message(
      id = "msg_grp_intro_${System.currentTimeMillis()}",
      conversationId = newGroup.id,
      senderId = _uiState.value.currentUser.id,
      senderName = _uiState.value.currentUser.name,
      text = "Welcome everyone to $groupName! 🎉",
      timestamp = formatCurrentTime(),
      status = MessageStatus.READ
    )

    _uiState.update {
      it.copy(
        conversations = listOf(newGroup) + it.conversations,
        messages = it.messages + (newGroup.id to listOf(introMsg)),
        activeConversationId = newGroup.id
      )
    }
    navigateTo(Screen.CHAT)
  }

  // Add Contact
  fun addNewContact(name: String, phone: String, email: String) {
    val newContact = User(
      id = "user_${System.currentTimeMillis()}",
      name = name,
      phone = phone,
      email = email,
      statusBio = "Available on MyChat",
      isOnline = true
    )
    _uiState.update {
      it.copy(contacts = listOf(newContact) + it.contacts)
    }
  }

  // Profile Edit
  fun updateUserProfile(name: String, statusBio: String, phone: String, email: String) {
    _uiState.update {
      it.copy(
        currentUser = it.currentUser.copy(
          name = name,
          statusBio = statusBio,
          phone = phone,
          email = email
        )
      )
    }
  }

  // Privacy & Settings toggles
  fun toggleReadReceipts(enabled: Boolean) {
    _uiState.update { it.copy(readReceiptsEnabled = enabled) }
  }

  fun toggleNotifications(enabled: Boolean) {
    _uiState.update { it.copy(notificationsEnabled = enabled) }
  }

  fun toggleVibration(enabled: Boolean) {
    _uiState.update { it.copy(vibrationEnabled = enabled) }
  }

  fun setLastSeenPrivacy(option: String) {
    _uiState.update { it.copy(lastSeenPrivacy = option) }
  }

  private fun formatCurrentTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
  }

  private fun formatCurrentDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
  }
}
