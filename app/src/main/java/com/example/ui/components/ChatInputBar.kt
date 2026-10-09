package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
  replyingTo: Message?,
  isRecording: Boolean,
  recordingDurationSec: Int,
  isDarkMode: Boolean,
  onCancelReply: () -> Unit,
  onSendMessage: (String, MessageType, String?, String?, Int, String?, String?) -> Unit,
  onStartRecording: () -> Unit,
  onCancelRecording: () -> Unit,
  onFinishRecording: () -> Unit,
  modifier: Modifier = Modifier
) {
  var text by remember { mutableStateOf("") }
  var showEmojiPicker by remember { mutableStateOf(false) }
  var showAttachmentSheet by remember { mutableStateOf(false) }

  val popularEmojis = listOf("😀", "😂", "🥰", "😎", "👍", "🔥", "❤️", "🎉", "👏", "🚀", "💯", "🙌", "🥳", "✨", "☕")

  val inputBg = if (isDarkMode) DarkSurfaceVariant else Color.White
  val textColor = if (isDarkMode) DarkTextPrimary else LightTextPrimary

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(if (isDarkMode) DarkBg else Color(0xFFF0F2F5))
      .padding(horizontal = 6.dp, vertical = 6.dp)
  ) {
    // Reply preview header
    AnimatedVisibility(visible = replyingTo != null) {
      replyingTo?.let { reply ->
        Surface(
          color = if (isDarkMode) DarkSurface else Color(0xFFE9EDEF),
          shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(IntrinsicSize.Min)
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(WhatsAppGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Replying to ${reply.senderName}",
                color = WhatsAppGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
              Text(
                text = reply.text,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1
              )
            }
            IconButton(
              onClick = onCancelReply,
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel reply",
                tint = if (isDarkMode) DarkTextSecondary else LightTextSecondary
              )
            }
          }
        }
      }
    }

    // Quick Emoji Bar
    AnimatedVisibility(visible = showEmojiPicker) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        popularEmojis.forEach { emoji ->
          Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier
              .clickable {
                text += emoji
              }
              .padding(4.dp)
          )
        }
      }
    }

    // Input Control Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isRecording) {
        // Voice recording active bar
        Row(
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDarkMode) DarkSurface else Color.White)
            .padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(Color(0xFFE53935))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "0:${if (recordingDurationSec < 10) "0$recordingDurationSec" else recordingDurationSec}",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE53935),
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.weight(1f))
          Text(
            text = "Recording audio...",
            color = if (isDarkMode) DarkTextSecondary else LightTextSecondary,
            fontSize = 13.sp
          )
          IconButton(onClick = onCancelRecording) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Cancel audio",
              tint = Color(0xFFE53935)
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Send Voice Note button
        FilledIconButton(
          onClick = onFinishRecording,
          colors = IconButtonDefaults.filledIconButtonColors(containerColor = WhatsAppGreen),
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send voice note",
            tint = Color.White
          )
        }

      } else {
        // Standard text input
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = inputBg,
          shadowElevation = 1.dp,
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { showEmojiPicker = !showEmojiPicker },
              modifier = Modifier.size(40.dp)
            ) {
              Icon(
                imageVector = if (showEmojiPicker) Icons.Default.Keyboard else Icons.Default.SentimentSatisfiedAlt,
                contentDescription = "Emojis",
                tint = if (isDarkMode) DarkTextSecondary else LightTextSecondary
              )
            }

            TextField(
              value = text,
              onValueChange = { text = it },
              placeholder = {
                Text(
                  "Message",
                  color = if (isDarkMode) DarkTextSecondary else LightTextSecondary,
                  fontSize = 15.sp
                )
              },
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
              ),
              modifier = Modifier.weight(1f)
            )

            // Attachment icon
            IconButton(
              onClick = { showAttachmentSheet = true },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Attach file",
                tint = if (isDarkMode) DarkTextSecondary else LightTextSecondary
              )
            }

            // Quick photo sharing button
            IconButton(
              onClick = {
                onSendMessage(
                  "Shared a photo",
                  MessageType.IMAGE,
                  null,
                  "Check out this photo preview! 📷",
                  0,
                  null,
                  null
                )
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Take photo",
                tint = if (isDarkMode) DarkTextSecondary else LightTextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Mic or Send circular FAB
        FilledIconButton(
          onClick = {
            if (text.isNotBlank()) {
              onSendMessage(text.trim(), MessageType.TEXT, null, null, 0, null, null)
              text = ""
              showEmojiPicker = false
            } else {
              onStartRecording()
            }
          },
          colors = IconButtonDefaults.filledIconButtonColors(containerColor = WhatsAppGreen),
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = if (text.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
            contentDescription = if (text.isNotBlank()) "Send message" else "Record voice message",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }

  // Attachment Bottom Sheet
  if (showAttachmentSheet) {
    ModalBottomSheet(
      onDismissRequest = { showAttachmentSheet = false }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 16.dp)
      ) {
        Text(
          "Share Content",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          AttachmentActionItem(
            label = "Document",
            icon = Icons.Default.Description,
            color = Color(0xFF7F66FF),
            onClick = {
              showAttachmentSheet = false
              onSendMessage(
                "Document",
                MessageType.DOCUMENT,
                null,
                null,
                0,
                "Project_Roadmap_Q4.pdf",
                "1.8 MB"
              )
            }
          )
          AttachmentActionItem(
            label = "Camera",
            icon = Icons.Default.CameraAlt,
            color = Color(0xFFE91E63),
            onClick = {
              showAttachmentSheet = false
              onSendMessage("Photo", MessageType.IMAGE, null, "Captured snapshot 📸", 0, null, null)
            }
          )
          AttachmentActionItem(
            label = "Gallery",
            icon = Icons.Default.Image,
            color = Color(0xFF9C27B0),
            onClick = {
              showAttachmentSheet = false
              onSendMessage("Gallery photo", MessageType.IMAGE, null, "From my gallery ✨", 0, null, null)
            }
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          AttachmentActionItem(
            label = "Audio",
            icon = Icons.Default.Headphones,
            color = Color(0xFFFF9800),
            onClick = {
              showAttachmentSheet = false
              onSendMessage("Voice memo", MessageType.AUDIO, null, null, 14, null, null)
            }
          )
          AttachmentActionItem(
            label = "Location",
            icon = Icons.Default.LocationOn,
            color = Color(0xFF00A884),
            onClick = {
              showAttachmentSheet = false
              onSendMessage("📍 Shared location: San Francisco, CA", MessageType.TEXT, null, null, 0, null, null)
            }
          )
          AttachmentActionItem(
            label = "Contact",
            icon = Icons.Default.Person,
            color = Color(0xFF03A9F4),
            onClick = {
              showAttachmentSheet = false
              onSendMessage("👤 Contact: Sarah Jenkins (+1 555-301-4920)", MessageType.TEXT, null, null, 0, null, null)
            }
          )
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun AttachmentActionItem(
  label: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable(onClick = onClick)
      .padding(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(CircleShape)
        .background(color),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = Color.White,
        modifier = Modifier.size(26.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      fontSize = 12.5.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
