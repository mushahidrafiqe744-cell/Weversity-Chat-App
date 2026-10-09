package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatWallpaper
import com.example.model.User
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
  currentUser: User,
  isDarkMode: Boolean,
  wallpaper: ChatWallpaper,
  readReceiptsEnabled: Boolean,
  notificationsEnabled: Boolean,
  vibrationEnabled: Boolean,
  lastSeenPrivacy: String,
  onBack: () -> Unit,
  onUpdateProfile: (String, String, String, String) -> Unit,
  onToggleDarkMode: () -> Unit,
  onSetWallpaper: (ChatWallpaper) -> Unit,
  onToggleReadReceipts: (Boolean) -> Unit,
  onToggleNotifications: (Boolean) -> Unit,
  onToggleVibration: (Boolean) -> Unit,
  onSetLastSeenPrivacy: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showQrDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showWallpaperDialog by remember { mutableStateOf(false) }

  // Edit fields
  var editName by remember { mutableStateOf(currentUser.name) }
  var editBio by remember { mutableStateOf(currentUser.statusBio) }
  var editPhone by remember { mutableStateOf(currentUser.phone) }
  var editEmail by remember { mutableStateOf(currentUser.email) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings", fontWeight = FontWeight.Bold, color = Color.White) },
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
      // Profile Hero Card
      item {
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              editName = currentUser.name
              editBio = currentUser.statusBio
              editPhone = currentUser.phone
              editEmail = currentUser.email
              showEditProfileDialog = true
            }
            .padding(vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AvatarView(name = currentUser.name, size = 64.dp, fontSize = 22)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                currentUser.statusBio,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
              )
              Text(currentUser.phone, fontSize = 12.sp, color = WhatsAppGreen)
            }
            IconButton(onClick = { showQrDialog = true }) {
              Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = WhatsAppGreen)
            }
          }
        }

        HorizontalDivider(
          thickness = 0.5.dp,
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(10.dp))
      }

      // Settings Group: Account & Privacy
      item {
        SectionTitle(title = "Account & Privacy")

        SettingsRowItem(
          icon = Icons.Default.Lock,
          title = "Privacy",
          subtitle = "Last seen ($lastSeenPrivacy), profile photo, read receipts",
          onClick = { showPrivacyDialog = true }
        )

        SettingsSwitchItem(
          icon = Icons.Default.DoneAll,
          title = "Read receipts",
          subtitle = "If turned off, you won't send or receive blue checkmarks",
          checked = readReceiptsEnabled,
          onCheckedChange = onToggleReadReceipts
        )

        Spacer(modifier = Modifier.height(14.dp))
      }

      // Settings Group: Appearance & Chats
      item {
        SectionTitle(title = "Chats & Appearance")

        SettingsSwitchItem(
          icon = Icons.Default.DarkMode,
          title = "Dark theme",
          subtitle = if (isDarkMode) "Dark mode is enabled" else "Light mode is enabled",
          checked = isDarkMode,
          onCheckedChange = { onToggleDarkMode() }
        )

        SettingsRowItem(
          icon = Icons.Default.Wallpaper,
          title = "Chat wallpaper",
          subtitle = "Theme: ${wallpaper.name.replace("_", " ")}",
          onClick = { showWallpaperDialog = true }
        )

        Spacer(modifier = Modifier.height(14.dp))
      }

      // Settings Group: Notifications
      item {
        SectionTitle(title = "Notifications")

        SettingsSwitchItem(
          icon = Icons.Default.Notifications,
          title = "Message notifications",
          subtitle = "Play sounds for incoming messages",
          checked = notificationsEnabled,
          onCheckedChange = onToggleNotifications
        )

        SettingsSwitchItem(
          icon = Icons.Default.Vibration,
          title = "Vibrate",
          subtitle = "Vibrate on receiving new chats",
          checked = vibrationEnabled,
          onCheckedChange = onToggleVibration
        )

        Spacer(modifier = Modifier.height(14.dp))
      }

      // Settings Group: Help & Info
      item {
        SectionTitle(title = "App Info")

        SettingsRowItem(
          icon = Icons.Default.HelpOutline,
          title = "Help & Support",
          subtitle = "FAQ, contact us, terms and privacy policy",
          onClick = { }
        )

        SettingsRowItem(
          icon = Icons.Default.Info,
          title = "MyChat for Android",
          subtitle = "Version 1.0.0 • Production Build",
          onClick = { }
        )

        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }

  // Dialog: Edit Profile
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("About / Bio") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editPhone,
            onValueChange = { editPhone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editEmail,
            onValueChange = { editEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editName.isNotBlank()) {
              onUpdateProfile(editName.trim(), editBio.trim(), editPhone.trim(), editEmail.trim())
              showEditProfileDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Dialog: QR Code
  if (showQrDialog) {
    AlertDialog(
      onDismissRequest = { showQrDialog = false },
      title = { Text("MyChat QR Code", fontWeight = FontWeight.Bold) },
      text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .size(160.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(130.dp))
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text(currentUser.phone, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Your friends can scan this code to start a chat with you on MyChat.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showQrDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
        ) {
          Text("Close")
        }
      }
    )
  }

  // Dialog: Privacy (Last seen)
  if (showPrivacyDialog) {
    val privacyOptions = listOf("Everyone", "My Contacts", "Nobody")
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Who can see my Last Seen?", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          privacyOptions.forEach { option ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onSetLastSeenPrivacy(option)
                  showPrivacyDialog = false
                }
                .padding(vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = lastSeenPrivacy == option,
                onClick = {
                  onSetLastSeenPrivacy(option)
                  showPrivacyDialog = false
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(option, fontSize = 15.sp)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showPrivacyDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Dialog: Wallpaper selection
  if (showWallpaperDialog) {
    AlertDialog(
      onDismissRequest = { showWallpaperDialog = false },
      title = { Text("Choose Wallpaper Theme", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          ChatWallpaper.values().forEach { wall ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  onSetWallpaper(wall)
                  showWallpaperDialog = false
                }
                .padding(vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = wallpaper == wall,
                onClick = {
                  onSetWallpaper(wall)
                  showWallpaperDialog = false
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(wall.name.replace("_", " "), fontSize = 15.sp)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showWallpaperDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun SectionTitle(title: String) {
  Text(
    text = title,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    color = WhatsAppTeal,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
  )
}

@Composable
private fun SettingsRowItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp)
        Text(subtitle, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun SettingsSwitchItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp)
        Text(subtitle, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WhatsAppGreen)
      )
    }
  }
}
