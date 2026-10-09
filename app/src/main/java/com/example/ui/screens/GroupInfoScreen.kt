package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.model.User
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoScreen(
  conversation: Conversation,
  currentUser: User,
  contacts: List<User>,
  isDarkMode: Boolean,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var showAddMemberDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Group info", fontWeight = FontWeight.Bold, color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = if (isDarkMode) MaterialTheme.colorScheme.surface else WhatsAppTeal
        )
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp, horizontal = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          AvatarView(name = conversation.name, isGroup = true, size = 88.dp)
          Spacer(modifier = Modifier.height(14.dp))
          Text(conversation.name, fontWeight = FontWeight.Bold, fontSize = 21.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Group • ${conversation.participantNames.size + 1} participants",
            fontSize = 13.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Description Card
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Description", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = WhatsAppTeal)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = conversation.description.ifBlank { "No description set yet. Tap to add group rules or topic." },
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Encryption notice
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppTeal, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text("Encryption", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Messages and calls are end-to-end encrypted.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Participants header
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "${conversation.participantNames.size + 1} participants",
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp
              )
              IconButton(onClick = { showAddMemberDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add member", tint = WhatsAppGreen)
              }
            }

            // You (Admin)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AvatarView(name = currentUser.name, size = 42.dp)
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("You", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(currentUser.statusBio, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Surface(
                color = WhatsAppTeal.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  "Group Admin",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = WhatsAppTeal,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Member list
      items(conversation.participantNames) { memberName ->
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AvatarView(name = memberName, size = 42.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(memberName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
              Text("Member", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))

        // Exit Group
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onBack() }
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(14.dp))
            Text("Exit group", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
          }
        }

        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }

  if (showAddMemberDialog) {
    AlertDialog(
      onDismissRequest = { showAddMemberDialog = false },
      title = { Text("Add Participant", fontWeight = FontWeight.Bold) },
      text = {
        Text("Select contacts to invite to ${conversation.name}.")
      },
      confirmButton = {
        Button(
          onClick = { showAddMemberDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
        ) {
          Text("Done")
        }
      }
    )
  }
}
