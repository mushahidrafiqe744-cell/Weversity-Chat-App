package com.example.data

import com.example.model.*

object SampleData {
  val currentUser = User(
    id = "user_me",
    name = "Alex Morgan",
    phone = "+1 (555) 234-8901",
    email = "alex.morgan@mychat.io",
    statusBio = "Building real-time apps with Jetpack Compose 🚀",
    isOnline = true
  )

  val sampleContacts = listOf(
    User(
      id = "user_sarah",
      name = "Sarah Jenkins",
      phone = "+1 (555) 301-4920",
      email = "sarah.j@example.com",
      statusBio = "Design lead @ Studio • Coffee lover ☕",
      isOnline = true,
      lastSeen = "online"
    ),
    User(
      id = "user_david",
      name = "David Chen",
      phone = "+1 (555) 782-9912",
      email = "david.c@example.com",
      statusBio = "Full-stack engineer & open-source tinkerer",
      isOnline = false,
      lastSeen = "today at 10:45 AM"
    ),
    User(
      id = "user_elena",
      name = "Elena Rostova",
      phone = "+1 (555) 819-3341",
      email = "elena.r@example.com",
      statusBio = "At the gym 🏋️ • Leave a voice note",
      isOnline = true,
      lastSeen = "online"
    ),
    User(
      id = "user_marcus",
      name = "Marcus Vance",
      phone = "+1 (555) 902-1144",
      email = "marcus.v@example.com",
      statusBio = "Available for urgent pings",
      isOnline = false,
      lastSeen = "yesterday at 9:15 PM"
    ),
    User(
      id = "user_priya",
      name = "Priya Patel",
      phone = "+1 (555) 431-7729",
      email = "priya.p@example.com",
      statusBio = "Life is good ✨",
      isOnline = true,
      lastSeen = "online"
    )
  )

  val initialConversations = listOf(
    Conversation(
      id = "conv_sarah",
      isGroup = false,
      name = "Sarah Jenkins",
      participantIds = listOf("user_sarah"),
      participantNames = listOf("Sarah Jenkins"),
      lastMessageText = "The new UI looks super clean! Let me send the updated assets.",
      lastMessageTime = "10:42 AM",
      lastMessageStatus = MessageStatus.READ,
      unreadCount = 2,
      isOnline = true,
      description = "Lead Product Designer"
    ),
    Conversation(
      id = "conv_dev_team",
      isGroup = true,
      name = "🚀 Core Dev Team",
      participantIds = listOf("user_david", "user_marcus", "user_priya"),
      participantNames = listOf("David Chen", "Marcus Vance", "Priya Patel"),
      lastMessageText = "David: WebSocket connection benchmark passed with 10k concurrent clients!",
      lastMessageTime = "10:30 AM",
      lastMessageStatus = MessageStatus.READ,
      unreadCount = 5,
      description = "Sprint discussions, PR reviews & architecture decisions."
    ),
    Conversation(
      id = "conv_david",
      isGroup = false,
      name = "David Chen",
      participantIds = listOf("user_david"),
      participantNames = listOf("David Chen"),
      lastMessageText = "🎤 Voice message (0:18)",
      lastMessageTime = "Yesterday",
      lastMessageStatus = MessageStatus.READ,
      unreadCount = 0,
      isOnline = false,
      description = "Backend Lead"
    ),
    Conversation(
      id = "conv_family",
      isGroup = true,
      name = "🏡 Family Chat",
      participantIds = listOf("user_elena", "user_sarah"),
      participantNames = listOf("Elena Rostova", "Sarah Jenkins"),
      lastMessageText = "Elena: Don't forget Sunday dinner at 6 PM! 🥘",
      lastMessageTime = "Yesterday",
      lastMessageStatus = MessageStatus.DELIVERED,
      unreadCount = 0,
      description = "Weekend dinners & family updates"
    ),
    Conversation(
      id = "conv_priya",
      isGroup = false,
      name = "Priya Patel",
      participantIds = listOf("user_priya"),
      participantNames = listOf("Priya Patel"),
      lastMessageText = "📄 Project_Specification_v2.pdf",
      lastMessageTime = "Tuesday",
      lastMessageStatus = MessageStatus.SENT,
      unreadCount = 0,
      isOnline = true,
      description = "Product Manager"
    )
  )

  fun getInitialMessages(convId: String): List<Message> {
    return when (convId) {
      "conv_sarah" -> listOf(
        Message(
          id = "msg_s1",
          conversationId = "conv_sarah",
          senderId = "user_sarah",
          senderName = "Sarah Jenkins",
          text = "Hey Alex! Did you get a chance to review the WhatsApp-style design tokens?",
          timestamp = "10:28 AM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_s2",
          conversationId = "conv_sarah",
          senderId = "user_me",
          senderName = "Alex Morgan",
          text = "Yes! The emerald green `#00A884` and dark surface tones feel authentic.",
          timestamp = "10:31 AM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_s3",
          conversationId = "conv_sarah",
          senderId = "user_sarah",
          senderName = "Sarah Jenkins",
          text = "Awesome! Also check out the voice message recording UI and checkmark indicators.",
          timestamp = "10:35 AM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_s4",
          conversationId = "conv_sarah",
          senderId = "user_sarah",
          senderName = "Sarah Jenkins",
          text = "The new UI looks super clean! Let me send the updated assets.",
          timestamp = "10:42 AM",
          status = MessageStatus.READ
        )
      )
      "conv_dev_team" -> listOf(
        Message(
          id = "msg_dt1",
          conversationId = "conv_dev_team",
          senderId = "user_marcus",
          senderName = "Marcus Vance",
          text = "Team, we just deployed the Socket.IO cluster on the Node backend.",
          timestamp = "09:40 AM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_dt2",
          conversationId = "conv_dev_team",
          senderId = "user_priya",
          senderName = "Priya Patel",
          text = "That's huge! Latency is down to sub-30ms across instances.",
          timestamp = "10:12 AM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_dt3",
          conversationId = "conv_dev_team",
          senderId = "user_david",
          senderName = "David Chen",
          text = "WebSocket connection benchmark passed with 10k concurrent clients!",
          timestamp = "10:30 AM",
          status = MessageStatus.READ
        )
      )
      "conv_david" -> listOf(
        Message(
          id = "msg_d1",
          conversationId = "conv_david",
          senderId = "user_me",
          senderName = "Alex Morgan",
          text = "Hey David, how's the MongoDB replica set holding up?",
          timestamp = "Yesterday 4:10 PM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_d2",
          conversationId = "conv_david",
          senderId = "user_david",
          senderName = "David Chen",
          text = "Smooth as butter. Here's a quick voice note explaining the index optimizations:",
          timestamp = "Yesterday 4:15 PM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_d3",
          conversationId = "conv_david",
          senderId = "user_david",
          senderName = "David Chen",
          text = "Voice message",
          timestamp = "Yesterday 4:16 PM",
          status = MessageStatus.READ,
          type = MessageType.AUDIO,
          audioDurationSec = 18
        )
      )
      "conv_priya" -> listOf(
        Message(
          id = "msg_p1",
          conversationId = "conv_priya",
          senderId = "user_priya",
          senderName = "Priya Patel",
          text = "Attached the product specification doc for MyChat release v1.0",
          timestamp = "Tuesday 2:10 PM",
          status = MessageStatus.READ
        ),
        Message(
          id = "msg_p2",
          conversationId = "conv_priya",
          senderId = "user_priya",
          senderName = "Priya Patel",
          text = "Project_Specification_v2.pdf",
          timestamp = "Tuesday 2:12 PM",
          status = MessageStatus.READ,
          type = MessageType.DOCUMENT,
          documentName = "MyChat_Specification_v2.pdf",
          documentSize = "2.4 MB"
        )
      )
      else -> listOf(
        Message(
          id = "msg_def1",
          conversationId = convId,
          senderId = "user_me",
          senderName = "Alex Morgan",
          text = "Hello! Started a new conversation on MyChat.",
          timestamp = "10:00 AM",
          status = MessageStatus.READ
        )
      )
    }
  }
}
