package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.theme.*

@Composable
fun MessageBubble(
  message: Message,
  isOutgoing: Boolean,
  isGroup: Boolean = false,
  isDarkMode: Boolean = false,
  onReply: (Message) -> Unit,
  onDelete: (Message) -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by androidx.compose.runtime.remember { mutableStateOf(false) }
  var isAudioPlaying by androidx.compose.runtime.remember { mutableStateOf(false) }

  val bubbleBg = when {
    isOutgoing && isDarkMode -> WhatsAppOutgoingDark
    isOutgoing && !isDarkMode -> WhatsAppOutgoingLight
    !isOutgoing && isDarkMode -> WhatsAppIncomingDark
    else -> WhatsAppIncomingLight
  }

  val textColor = when {
    isDarkMode -> DarkTextPrimary
    else -> LightTextPrimary
  }

  val bubbleShape = if (isOutgoing) {
    RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 2.dp)
  } else {
    RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 2.dp, bottomEnd = 14.dp)
  }

  val senderColor = remember(message.senderName) {
    val colors = listOf(
      Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF8E24AA),
      Color(0xFFFB8C00), Color(0xFF00897B), Color(0xFFD81B60)
    )
    colors[kotlin.math.abs(message.senderName.hashCode()) % colors.size]
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 3.dp),
    horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
  ) {
    Box {
      Surface(
        shape = bubbleShape,
        color = bubbleBg,
        shadowElevation = 1.dp,
        modifier = Modifier
          .widthIn(min = 80.dp, max = 310.dp)
          .clickable { showMenu = true }
      ) {
        Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)) {

          // Group sender label
          if (!isOutgoing && isGroup) {
            Text(
              text = message.senderName,
              color = senderColor,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp,
              modifier = Modifier.padding(bottom = 2.dp)
            )
          }

          // Reply snippet if message is a reply
          message.replyTo?.let { reply ->
            Surface(
              color = if (isDarkMode) Color(0x33000000) else Color(0x1F000000),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
            ) {
              Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                  modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(WhatsAppGreen)
                )
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                  Text(
                    text = reply.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = WhatsAppGreen
                  )
                  Text(
                    text = reply.text,
                    maxLines = 1,
                    fontSize = 11.5.sp,
                    color = textColor.copy(alpha = 0.8f)
                  )
                }
              }
            }
          }

          // Message Content by Type
          if (message.isDeleted) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Block,
                contentDescription = null,
                tint = if (isDarkMode) DarkTextSecondary else LightTextSecondary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(Modifier.width(6.dp))
              Text(
                text = "This message was deleted",
                fontSize = 13.5.sp,
                fontStyle = FontStyle.Italic,
                color = if (isDarkMode) DarkTextSecondary else LightTextSecondary
              )
            }
          } else {
            when (message.type) {
              MessageType.TEXT -> {
                Text(
                  text = message.text,
                  color = textColor,
                  fontSize = 14.5.sp,
                  lineHeight = 19.sp
                )
              }
              MessageType.AUDIO -> {
                VoiceNoteBubble(
                  durationSec = message.audioDurationSec,
                  isPlaying = isAudioPlaying,
                  isOutgoing = isOutgoing,
                  isDarkMode = isDarkMode,
                  onTogglePlay = { isAudioPlaying = !isAudioPlaying }
                )
              }
              MessageType.DOCUMENT -> {
                DocumentBubble(
                  name = message.documentName ?: "Document.pdf",
                  size = message.documentSize ?: "1.2 MB",
                  isDarkMode = isDarkMode
                )
              }
              MessageType.IMAGE -> {
                ImageBubble(
                  caption = message.mediaCaption,
                  textColor = textColor,
                  isDarkMode = isDarkMode
                )
              }
            }
          }

          // Timestamp and Delivery Status Tick
          Row(
            modifier = Modifier
              .align(Alignment.End)
              .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = message.timestamp,
              fontSize = 11.sp,
              color = if (isDarkMode) DarkTextSecondary else LightTextSecondary
            )
            if (isOutgoing && !message.isDeleted) {
              Spacer(modifier = Modifier.width(3.dp))
              StatusTick(status = message.status)
            }
          }
        }
      }

      // Dropdown Context Menu for message interactions
      DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
      ) {
        DropdownMenuItem(
          text = { Text("Reply") },
          leadingIcon = { Icon(Icons.Default.Reply, contentDescription = null) },
          onClick = {
            showMenu = false
            onReply(message)
          }
        )
        DropdownMenuItem(
          text = { Text("Star") },
          leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
          onClick = { showMenu = false }
        )
        DropdownMenuItem(
          text = { Text("Copy") },
          leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
          onClick = { showMenu = false }
        )
        if (isOutgoing && !message.isDeleted) {
          HorizontalDivider()
          DropdownMenuItem(
            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
            leadingIcon = {
              Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            },
            onClick = {
              showMenu = false
              onDelete(message)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun VoiceNoteBubble(
  durationSec: Int,
  isPlaying: Boolean,
  isOutgoing: Boolean,
  isDarkMode: Boolean,
  onTogglePlay: () -> Unit
) {
  val waveformHeights = remember {
    listOf(12, 18, 8, 22, 16, 26, 14, 20, 24, 10, 19, 15, 23, 11, 17, 25, 8, 14, 21, 13)
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    // Play / Pause round button
    FilledIconButton(
      onClick = onTogglePlay,
      colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = WhatsAppGreen
      ),
      modifier = Modifier.size(36.dp)
    ) {
      Icon(
        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = if (isPlaying) "Pause" else "Play",
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Waveform bars
    Column(modifier = Modifier.weight(1f)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        waveformHeights.forEachIndexed { index, height ->
          val barColor = if (isPlaying && index < waveformHeights.size / 2) {
            WhatsAppGreen
          } else {
            if (isDarkMode) Color.LightGray.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.6f)
          }
          Box(
            modifier = Modifier
              .width(2.5.dp)
              .height(height.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(barColor)
          )
        }
      }

      Spacer(modifier = Modifier.height(3.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (isPlaying) "0:08" else "0:${if (durationSec < 10) "0$durationSec" else durationSec}",
          fontSize = 11.sp,
          color = if (isDarkMode) DarkTextSecondary else LightTextSecondary
        )
        Icon(
          imageVector = Icons.Default.Mic,
          contentDescription = null,
          tint = WhatsAppGreen,
          modifier = Modifier.size(13.dp)
        )
      }
    }
  }
}

@Composable
private fun DocumentBubble(
  name: String,
  size: String,
  isDarkMode: Boolean
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(if (isDarkMode) Color(0x22FFFFFF) else Color(0x11000000))
      .padding(8.dp)
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(6.dp))
        .background(Color(0xFFE53935)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Description,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        maxLines = 1
      )
      Text(
        text = size,
        fontSize = 11.sp,
        color = if (isDarkMode) DarkTextSecondary else LightTextSecondary
      )
    }

    IconButton(
      onClick = { },
      modifier = Modifier.size(32.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Download,
        contentDescription = "Download document",
        tint = WhatsAppGreen
      )
    }
  }
}

@Composable
private fun ImageBubble(
  caption: String?,
  textColor: Color,
  isDarkMode: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp)
  ) {
    // Beautiful placeholder photo preview
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF1E88E5).copy(alpha = 0.85f)),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.Image,
          contentDescription = "Shared photo",
          tint = Color.White,
          modifier = Modifier.size(44.dp)
        )
        Text(
          text = "Shared Photo Preview",
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    caption?.let {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = it,
        color = textColor,
        fontSize = 14.sp
      )
    }
  }
}
