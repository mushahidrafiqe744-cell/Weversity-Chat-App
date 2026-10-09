package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WhatsAppOnline

@Composable
fun AvatarView(
  name: String,
  isGroup: Boolean = false,
  isOnline: Boolean = false,
  size: Dp = 48.dp,
  fontSize: Int = 18,
  modifier: Modifier = Modifier
) {
  val avatarColors = listOf(
    Color(0xFF00A884),
    Color(0xFF1E88E5),
    Color(0xFFE53935),
    Color(0xFFFB8C00),
    Color(0xFF8E24AA),
    Color(0xFF00897B),
    Color(0xFF3949AB)
  )
  val colorIndex = (name.hashCode().coerceAtLeast(0)) % avatarColors.size
  val bgCol = avatarColors[colorIndex]

  Box(
    modifier = modifier.size(size)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(CircleShape)
        .background(bgCol),
      contentAlignment = Alignment.Center
    ) {
      if (isGroup) {
        Icon(
          imageVector = Icons.Default.Group,
          contentDescription = "Group",
          tint = Color.White,
          modifier = Modifier.size(size * 0.55f)
        )
      } else {
        val initials = name.trim().split(" ")
          .take(2)
          .mapNotNull { it.firstOrNull()?.uppercase() }
          .joinToString("")
        Text(
          text = if (initials.isNotEmpty()) initials else "?",
          color = Color.White,
          fontSize = fontSize.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    if (isOnline && !isGroup) {
      Box(
        modifier = Modifier
          .size(size * 0.3f)
          .align(Alignment.BottomEnd)
          .clip(CircleShape)
          .background(WhatsAppOnline)
          .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
      )
    }
  }
}
