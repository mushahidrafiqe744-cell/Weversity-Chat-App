package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.AvatarView
import com.example.ui.components.StatusTick
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  conversations: List<Conversation>,
  selectedTab: DashboardTab,
  searchQuery: String,
  isSearchOpen: Boolean,
  isDarkMode: Boolean,
  currentUser: User,
  onTabSelected: (DashboardTab) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onToggleSearch: () -> Unit,
  onToggleDarkMode: () -> Unit,
  onOpenConversation: (String) -> Unit,
  onNewChatClicked: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

  val filteredConversations = remember(conversations, searchQuery, selectedTab) {
    conversations.filter { conv ->
      val matchesTab = when (selectedTab) {
        DashboardTab.CHATS -> !conv.isGroup
        DashboardTab.GROUPS -> conv.isGroup
        DashboardTab.STATUS, DashboardTab.CALLS -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
        conv.name.contains(searchQuery, ignoreCase = true) ||
        conv.lastMessageText.contains(searchQuery, ignoreCase = true)
      matchesTab && matchesSearch
    }
  }

  val totalUnread = remember(conversations) {
    conversations.filter { !it.isGroup }.sumOf { it.unreadCount }
  }
  val groupUnread = remember(conversations) {
    conversations.filter { it.isGroup }.sumOf { it.unreadCount }
  }

  Scaffold(
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(if (isDarkMode) DarkSurface else WhatsAppTeal)
      ) {
        // Main App Bar
        TopAppBar(
          title = {
            if (isSearchOpen) {
              TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search chats...", color = Color.White.copy(alpha = 0.7f)) },
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
              Text(
                "MyChat",
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                color = Color.White
              )
            }
          },
          navigationIcon = {
            if (isSearchOpen) {
              IconButton(onClick = onToggleSearch) {
                Icon(
                  Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Close search",
                  tint = Color.White
                )
              }
            }
          },
          actions = {
            if (!isSearchOpen) {
              IconButton(onClick = onToggleSearch) {
                Icon(
                  Icons.Default.Search,
                  contentDescription = "Search",
                  tint = Color.White
                )
              }
            }
            IconButton(onClick = onToggleDarkMode) {
              Icon(
                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = Color.White
              )
            }
            IconButton(onClick = { showMenu = true }) {
              Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.White
              )
            }

            DropdownMenu(
              expanded = showMenu,
              onDismissRequest = { showMenu = false }
            ) {
              DropdownMenuItem(
                text = { Text("New group") },
                leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                onClick = {
                  showMenu = false
                  onNewChatClicked()
                }
              )
              DropdownMenuItem(
                text = { Text("Profile") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                onClick = {
                  showMenu = false
                  onNavigateToProfile()
                }
              )
              DropdownMenuItem(
                text = { Text("Settings") },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                onClick = {
                  showMenu = false
                  onNavigateToSettings()
                }
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
          )
        )

        // WhatsApp Signature Tabs Row
        TabRow(
          selectedTabIndex = selectedTab.ordinal,
          containerColor = if (isDarkMode) DarkSurface else WhatsAppTeal,
          contentColor = Color.White
        ) {
          Tab(
            selected = selectedTab == DashboardTab.CHATS,
            onClick = { onTabSelected(DashboardTab.CHATS) },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("CHATS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (totalUnread > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Badge(containerColor = WhatsAppGreen, contentColor = Color.White) {
                    Text(totalUnread.toString())
                  }
                }
              }
            }
          )
          Tab(
            selected = selectedTab == DashboardTab.GROUPS,
            onClick = { onTabSelected(DashboardTab.GROUPS) },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("GROUPS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (groupUnread > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Badge(containerColor = WhatsAppGreen, contentColor = Color.White) {
                    Text(groupUnread.toString())
                  }
                }
              }
            }
          )
          Tab(
            selected = selectedTab == DashboardTab.STATUS,
            onClick = { onTabSelected(DashboardTab.STATUS) },
            text = { Text("STATUS", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
          )
          Tab(
            selected = selectedTab == DashboardTab.CALLS,
            onClick = { onTabSelected(DashboardTab.CALLS) },
            text = { Text("CALLS", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
          )
        }
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNewChatClicked,
        containerColor = WhatsAppGreen,
        contentColor = Color.White,
        shape = CircleShape,
        modifier = Modifier.testTag("new_chat_fab")
      ) {
        Icon(
          imageVector = if (selectedTab == DashboardTab.CALLS) Icons.Default.Call else Icons.Default.Chat,
          contentDescription = "New chat"
        )
      }
    }
  ) { padding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (selectedTab) {
        DashboardTab.CHATS, DashboardTab.GROUPS -> {
          if (filteredConversations.isEmpty()) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.ChatBubbleOutline,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = if (searchQuery.isNotBlank()) "No conversations found" else "No conversations yet",
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Tap the green button below to start a chat",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }
            }
          } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
              items(filteredConversations, key = { it.id }) { conv ->
                ConversationItem(
                  conversation = conv,
                  isDarkMode = isDarkMode,
                  onClick = { onOpenConversation(conv.id) }
                )
                HorizontalDivider(
                  modifier = Modifier.padding(start = 74.dp),
                  thickness = 0.5.dp,
                  color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
              }
            }
          }
        }
        DashboardTab.STATUS -> {
          StatusTabContent(currentUser = currentUser, isDarkMode = isDarkMode)
        }
        DashboardTab.CALLS -> {
          CallsTabContent(isDarkMode = isDarkMode)
        }
      }
    }
  }
}

@Composable
private fun ConversationItem(
  conversation: Conversation,
  isDarkMode: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarView(
      name = conversation.name,
      isGroup = conversation.isGroup,
      isOnline = conversation.isOnline,
      size = 52.dp
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = conversation.name,
          fontWeight = FontWeight.SemiBold,
          fontSize = 16.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.weight(1f, fill = false)
        )
        Text(
          text = conversation.lastMessageTime,
          fontSize = 12.sp,
          color = if (conversation.unreadCount > 0) WhatsAppGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (conversation.typing) {
            Text(
              text = "typing...",
              fontSize = 13.5.sp,
              color = WhatsAppGreen,
              fontWeight = FontWeight.Medium,
              fontStyle = FontStyle.Italic
            )
          } else {
            if (!conversation.isGroup && conversation.unreadCount == 0) {
              StatusTick(status = conversation.lastMessageStatus)
              Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
              text = conversation.lastMessageText,
              fontSize = 13.5.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        if (conversation.unreadCount > 0) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(WhatsAppGreen)
              .padding(horizontal = 7.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = conversation.unreadCount.toString(),
              color = Color.White,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatusTabContent(currentUser: User, isDarkMode: Boolean) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 12.dp)
  ) {
    item {
      // My Status row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box {
          AvatarView(name = currentUser.name, size = 52.dp)
          Box(
            modifier = Modifier
              .size(20.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(WhatsAppGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text("My Status", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
          Text("Tap to add status update", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
      )

      Text(
        text = "Recent updates",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )
    }

    val sampleStatuses = listOf(
      Triple("Sarah Jenkins", "Today, 08:30 AM", "Designing MyChat UI components ✨"),
      Triple("David Chen", "Today, 07:15 AM", "Morning workout complete 🏃‍♂️"),
      Triple("Elena Rostova", "Yesterday, 11:20 PM", "Weekend getaway 🌄")
    )

    items(sampleStatuses) { (name, time, bio) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AvatarView(name = name, size = 52.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.5.sp)
          Text(time, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(bio, fontSize = 12.sp, color = WhatsAppGreen)
        }
      }
    }
  }
}

@Composable
private fun CallsTabContent(isDarkMode: Boolean) {
  val sampleCalls = listOf(
    Triple("Sarah Jenkins", "Today, 10:14 AM", true),
    Triple("David Chen", "Yesterday, 04:30 PM", false),
    Triple("Marcus Vance", "Monday, 09:12 AM", true),
    Triple("Priya Patel", "May 12, 11:45 AM", false)
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 8.dp)
  ) {
    items(sampleCalls) { (name, time, isIncoming) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AvatarView(name = name, size = 50.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isIncoming) Icons.AutoMirrored.Filled.CallReceived else Icons.AutoMirrored.Filled.CallMade,
              contentDescription = null,
              tint = if (isIncoming) Color(0xFFE53935) else WhatsAppGreen,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(time, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
        IconButton(onClick = { }) {
          Icon(Icons.Default.Call, contentDescription = "Call", tint = WhatsAppTeal)
        }
      }
    }
  }
}
