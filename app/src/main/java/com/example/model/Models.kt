package com.example.model

enum class MessageStatus {
  SENDING,
  SENT,       // Single gray check
  DELIVERED,  // Double gray check
  READ        // Double blue check
}

enum class MessageType {
  TEXT,
  IMAGE,
  AUDIO,
  DOCUMENT
}

data class User(
  val id: String,
  val name: String,
  val phone: String = "+1 (555) 019-2834",
  val email: String = "",
  val avatarUrl: String = "",
  val statusBio: String = "Hey there! I am using MyChat.",
  val isOnline: Boolean = true,
  val lastSeen: String = "Just now"
)

data class Message(
  val id: String,
  val conversationId: String,
  val senderId: String,
  val senderName: String,
  val text: String,
  val timestamp: String,
  val timestampMillis: Long = System.currentTimeMillis(),
  val status: MessageStatus = MessageStatus.READ,
  val type: MessageType = MessageType.TEXT,
  val mediaUrl: String? = null,
  val mediaCaption: String? = null,
  val audioDurationSec: Int = 0,
  val documentName: String? = null,
  val documentSize: String? = null,
  val replyTo: Message? = null,
  val isDeleted: Boolean = false
)

data class Conversation(
  val id: String,
  val isGroup: Boolean = false,
  val name: String,
  val avatarUrl: String = "",
  val participantIds: List<String> = emptyList(),
  val participantNames: List<String> = emptyList(),
  val lastMessageText: String = "",
  val lastMessageTime: String = "",
  val lastMessageStatus: MessageStatus = MessageStatus.READ,
  val unreadCount: Int = 0,
  val isOnline: Boolean = false,
  val typing: Boolean = false,
  val description: String = ""
)

enum class Screen {
  LANDING,
  AUTH,
  DASHBOARD,
  CHAT,
  CONTACTS,
  GROUP_INFO,
  SETTINGS,
  PROFILE
}

enum class DashboardTab {
  CHATS,
  GROUPS,
  STATUS,
  CALLS
}

enum class ChatWallpaper {
  CLASSIC_DOODLE,
  EMERALD,
  MIDNIGHT,
  CLEAN_SAND
}
