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
import com.example.model.User
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
  contacts: List<User>,
  isDarkMode: Boolean,
  onBack: () -> Unit,
  onSelectContact: (User) -> Unit,
  onCreateGroup: (String, List<String>) -> Unit,
  onAddContact: (String, String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var searchQuery by remember { mutableStateOf("") }
  var isSearchOpen by remember { mutableStateOf(false) }
  var showNewGroupDialog by remember { mutableStateOf(false) }
  var showNewContactDialog by remember { mutableStateOf(false) }

  // New Group dialog state
  var newGroupName by remember { mutableStateOf("") }
  val selectedMembers = remember { mutableStateListOf<String>() }

  // New Contact dialog state
  var contactName by remember { mutableStateOf("") }
  var contactPhone by remember { mutableStateOf("") }
  var contactEmail by remember { mutableStateOf("") }

  val filteredContacts = remember(contacts, searchQuery) {
    if (searchQuery.isBlank()) contacts
    else contacts.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery, ignoreCase = true) ||
        it.statusBio.contains(searchQuery, ignoreCase = true)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          if (isSearchOpen) {
            TextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search contacts...", color = Color.White.copy(alpha = 0.7f)) },
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
            Column {
              Text("Select contact", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
              Text("${contacts.size} contacts", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          if (isSearchOpen) {
            IconButton(onClick = {
              isSearchOpen = false
              searchQuery = ""
            }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
          } else {
            IconButton(onClick = { isSearchOpen = true }) {
              Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }
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
      if (!isSearchOpen || searchQuery.isBlank()) {
        item {
          // Action 1: New Group
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showNewGroupDialog = true }
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("New group", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
          }

          // Action 2: New Contact
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showNewContactDialog = true }
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("New contact", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
          }

          HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
          )

          Text(
            text = "Contacts on MyChat",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }
      }

      items(filteredContacts, key = { it.id }) { contact ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectContact(contact) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AvatarView(
            name = contact.name,
            isOnline = contact.isOnline,
            size = 46.dp
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = contact.name,
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.5.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = contact.statusBio,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1
            )
          }
          if (contact.isOnline) {
            Text(
              text = "online",
              fontSize = 11.5.sp,
              color = WhatsAppGreen,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }

  // Dialog: Create New Group
  if (showNewGroupDialog) {
    AlertDialog(
      onDismissRequest = { showNewGroupDialog = false },
      title = { Text("New Group", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = newGroupName,
            onValueChange = { newGroupName = it },
            label = { Text("Group Subject") },
            placeholder = { Text("e.g. Design Team, Family") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text("Select Members:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))
          contacts.forEach { user ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  if (selectedMembers.contains(user.id)) selectedMembers.remove(user.id)
                  else selectedMembers.add(user.id)
                }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = selectedMembers.contains(user.id),
                onCheckedChange = { checked ->
                  if (checked) selectedMembers.add(user.id) else selectedMembers.remove(user.id)
                }
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(user.name, fontSize = 14.sp)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newGroupName.isNotBlank() && selectedMembers.isNotEmpty()) {
              onCreateGroup(newGroupName.trim(), selectedMembers.toList())
              showNewGroupDialog = false
              newGroupName = ""
              selectedMembers.clear()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
        ) {
          Text("Create")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewGroupDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Dialog: Add New Contact
  if (showNewContactDialog) {
    AlertDialog(
      onDismissRequest = { showNewContactDialog = false },
      title = { Text("New Contact", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = contactName,
            onValueChange = { contactName = it },
            label = { Text("Contact Name") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = contactPhone,
            onValueChange = { contactPhone = it },
            label = { Text("Phone Number") },
            placeholder = { Text("+1 (555) ...") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = contactEmail,
            onValueChange = { contactEmail = it },
            label = { Text("Email (optional)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
              onAddContact(contactName.trim(), contactPhone.trim(), contactEmail.trim())
              showNewContactDialog = false
              contactName = ""
              contactPhone = ""
              contactEmail = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
        ) {
          Text("Save Contact")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewContactDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
