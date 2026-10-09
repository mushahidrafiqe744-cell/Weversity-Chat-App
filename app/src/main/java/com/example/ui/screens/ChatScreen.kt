package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.AvatarView
import com.example.ui.components.ChatInputBar
import com.example.ui.components.MessageBubble
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  conversation: Conversation,
  messages: List<Message>,
  currentUser: User,
  isDarkMode: Boolean,
  wallpaper: ChatWallpaper,
  replyingTo: Message?,
  isRecordingAudio: Boolean,
  recordingDurationSec: Int,
  onBack: () -> Unit,
  onSendMessage: (String, MessageType, String?, String?, Int, String?, String?) -> Unit,
  onSetReplyingTo: (Message?) -> Unit,
  onDeleteMessage: (String) -> Unit,
  onStartVoiceRecording: () -> Unit,
  onCancelVoiceRecording: () -> Unit,
  onFinishVoiceRecording: () -> Unit,
  onOpenGroupInfo: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler {
    onBack()
  }

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var showMenu by remember { mutableStateOf(false) }
  var callBannerText by remember { mutableStateOf<String?>(null) }
  var inChatSearchQuery by remember { mutableStateOf("") }
  var isInChatSearchOpen by remember { mutableStateOf(false) }

  // Auto scroll to bottom when new messages arrive
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val chatBgColor = when (wallpaper) {
    ChatWallpaper.CLASSIC_DOODLE -> if (isDarkMode) WhatsAppChatBgDark else WhatsAppChatBgLight
    ChatWallpaper.EMERALD -> if (isDarkMode) Color(0xFF07271F) else Color(0xFFE2F4EE)
    ChatWallpaper.MIDNIGHT -> Color(0xFF0B141A)
    ChatWallpaper.CLEAN_SAND -> Color(0xFFF7F4EB)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          if (isInChatSearchOpen) {
            TextField(
              value = inChatSearchQuery,
              onValueChange = { inChatSearchQuery = it },
              placeholder = { Text("Search in chat...", color = Color.White.copy(alpha = 0.7f)) },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable {
                  if (conversation.isGroup) onOpenGroupInfo()
                }
            ) {
              AvatarView(
                name = conversation.name,
                isGroup = conversation.isGroup,
                isOnline = conversation.isOnline,
                size = 38.dp,
                fontSize = 14
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = conversation.name,
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1
                )
                Text(
                  text = when {
                    conversation.typing -> "typing..."
                    conversation.isGroup -> "${conversation.participantNames.size} participants"
                    conversation.isOnline -> "online"
                    else -> "last seen today"
                  },
                  color = if (conversation.typing) WhatsAppGreen else Color.White.copy(alpha = 0.75f),
                  fontSize = 12.sp,
                  fontStyle = if (conversation.typing) FontStyle.Italic else FontStyle.Normal
                )
              }
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        actions = {
          if (isInChatSearchOpen) {
            IconButton(onClick = {
              isInChatSearchOpen = false
              inChatSearchQuery = ""
            }) {
              Icon(Icons.Default.Close, contentDescription = "Close search", tint = Color.White)
            }
          } else {
            IconButton(onClick = {
              callBannerText = "Starting encrypted video call with ${conversation.name}..."
            }) {
              Icon(Icons.Default.Videocam, contentDescription = "Video call", tint = Color.White)
            }
            IconButton(onClick = {
              callBannerText = "Starting encrypted voice call with ${conversation.name}..."
            }) {
              Icon(Icons.Default.Call, contentDescription = "Voice call", tint = Color.White)
            }
            IconButton(onClick = { showMenu = true }) {
              Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
            }

            DropdownMenu(
              expanded = showMenu,
              onDismissRequest = { showMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text(if (conversation.isGroup) "Group info" else "View contact") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                onClick = {
                  showMenu = false
                  onOpenGroupInfo()
                }
              )
              DropdownMenuItem(
                text = { Text("Search messages") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                onClick = {
                  showMenu = false
                  isInChatSearchOpen = true
                }
              )
              DropdownMenuItem(
                text = { Text("Clear chat") },
                leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null) },
                onClick = {
                  showMenu = false
                  callBannerText = "Chat cleared"
                }
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = if (isDarkMode) DarkSurface else WhatsAppTeal
        )
      )
    },
    bottomBar = {
      ChatInputBar(
        replyingTo = replyingTo,
        isRecording = isRecordingAudio,
        recordingDurationSec = recordingDurationSec,
        isDarkMode = isDarkMode,
        onCancelReply = { onSetReplyingTo(null) },
        onSendMessage = onSendMessage,
        onStartRecording = onStartVoiceRecording,
        onCancelRecording = onCancelVoiceRecording,
        onFinishRecording = onFinishVoiceRecording
      )
    }
  ) { padding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .background(chatBgColor)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Notification/Call Banner
        AnimatedVisibility(visible = callBannerText != null) {
          callBannerText?.let { banner ->
            Surface(
              color = WhatsAppTealDark,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(banner, color = Color.White, fontSize = 13.sp)
                IconButton(
                  onClick = { callBannerText = null },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                }
              }
            }
          }
        }

        // Filtered messages
        val displayedMessages = remember(messages, inChatSearchQuery) {
          if (inChatSearchQuery.isBlank()) messages
          else messages.filter { it.text.contains(inChatSearchQuery, ignoreCase = true) }
        }

        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Encryption Notice Banner
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkMode) Color(0x33000000) else Color(0x22FFE082),
                modifier = Modifier.widthIn(max = 320.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFF57F17),
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even MyChat, can read or listen to them.",
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = if (isDarkMode) DarkTextSecondary else Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                  )
                }
              }
            }
          }

          // Date Header
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkMode) Color(0x44000000) else Color(0x22000000),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "TODAY",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDarkMode) DarkTextSecondary else LightTextSecondary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }
          }

          items(displayedMessages, key = { it.id }) { msg ->
            MessageBubble(
              message = msg,
              isOutgoing = msg.senderId == currentUser.id,
              isGroup = conversation.isGroup,
              isDarkMode = isDarkMode,
              onReply = onSetReplyingTo,
              onDelete = { onDeleteMessage(it.id) }
            )
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }
    }
  }
}
