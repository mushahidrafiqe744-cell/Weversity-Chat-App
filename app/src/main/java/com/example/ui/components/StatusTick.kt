package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.MessageStatus
import com.example.ui.theme.WhatsAppBlueCheck
import com.example.ui.theme.WhatsAppGrayCheck

@Composable
fun StatusTick(
  status: MessageStatus,
  modifier: Modifier = Modifier
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    when (status) {
      MessageStatus.SENDING -> {
        Icon(
          imageVector = Icons.Default.AccessTime,
          contentDescription = "Sending",
          tint = WhatsAppGrayCheck,
          modifier = Modifier.size(13.dp)
        )
      }
      MessageStatus.SENT -> {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Sent",
          tint = WhatsAppGrayCheck,
          modifier = Modifier.size(15.dp)
        )
      }
      MessageStatus.DELIVERED -> {
        Icon(
          imageVector = Icons.Default.DoneAll,
          contentDescription = "Delivered",
          tint = WhatsAppGrayCheck,
          modifier = Modifier.size(16.dp)
        )
      }
      MessageStatus.READ -> {
        Icon(
          imageVector = Icons.Default.DoneAll,
          contentDescription = "Read",
          tint = WhatsAppBlueCheck,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
