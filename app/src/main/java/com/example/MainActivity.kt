package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: ChatViewModel = viewModel()
      val state by viewModel.uiState.collectAsStateWithLifecycle()

      MyApplicationTheme(darkTheme = state.isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MyChatApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun MyChatApp(viewModel: ChatViewModel) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  AnimatedContent(
    targetState = state.currentScreen,
    transitionSpec = {
      fadeIn() togetherWith fadeOut()
    },
    label = "ScreenTransition"
  ) { targetScreen ->
    when (targetScreen) {
      Screen.LANDING -> {
        LandingScreen(
          isDarkMode = state.isDarkMode,
          onGetStarted = { viewModel.navigateTo(Screen.DASHBOARD) },
          onSignIn = { viewModel.navigateTo(Screen.AUTH) }
        )
      }
      Screen.AUTH -> {
        AuthScreen(
          onSuccessAuth = { user ->
            viewModel.updateUserProfile(user.name, user.statusBio, user.phone, user.email)
            viewModel.navigateTo(Screen.DASHBOARD)
          },
          onBack = { viewModel.navigateBack() }
        )
      }
      Screen.DASHBOARD -> {
        DashboardScreen(
          conversations = state.conversations,
          selectedTab = state.selectedTab,
          searchQuery = state.searchQuery,
          isSearchOpen = state.isSearchOpen,
          isDarkMode = state.isDarkMode,
          currentUser = state.currentUser,
          onTabSelected = { viewModel.setTab(it) },
          onSearchQueryChanged = { viewModel.setSearchQuery(it) },
          onToggleSearch = { viewModel.toggleSearch() },
          onToggleDarkMode = { viewModel.toggleDarkMode() },
          onOpenConversation = { viewModel.openConversation(it) },
          onNewChatClicked = { viewModel.navigateTo(Screen.CONTACTS) },
          onNavigateToSettings = { viewModel.navigateTo(Screen.SETTINGS) },
          onNavigateToProfile = { viewModel.navigateTo(Screen.PROFILE) }
        )
      }
      Screen.CHAT -> {
        val currentConv = state.conversations.find { it.id == state.activeConversationId }
          ?: state.conversations.firstOrNull()

        if (currentConv != null) {
          val currentMessages = state.messages[currentConv.id] ?: emptyList()
          ChatScreen(
            conversation = currentConv,
            messages = currentMessages,
            currentUser = state.currentUser,
            isDarkMode = state.isDarkMode,
            wallpaper = state.wallpaper,
            replyingTo = state.replyingTo,
            isRecordingAudio = state.isRecordingAudio,
            recordingDurationSec = state.recordingDurationSec,
            onBack = { viewModel.navigateBack() },
            onSendMessage = { text, type, mediaUrl, caption, audioDuration, docName, docSize ->
              viewModel.sendMessage(text, type, mediaUrl, caption, audioDuration, docName, docSize)
            },
            onSetReplyingTo = { viewModel.setReplyingTo(it) },
            onDeleteMessage = { msgId -> viewModel.deleteMessage(currentConv.id, msgId) },
            onStartVoiceRecording = { viewModel.startVoiceRecording() },
            onCancelVoiceRecording = { viewModel.cancelVoiceRecording() },
            onFinishVoiceRecording = { viewModel.finishAndSendVoiceRecording() },
            onOpenGroupInfo = { viewModel.navigateTo(Screen.GROUP_INFO) }
          )
        } else {
          viewModel.navigateTo(Screen.DASHBOARD)
        }
      }
      Screen.CONTACTS -> {
        ContactsScreen(
          contacts = state.contacts,
          isDarkMode = state.isDarkMode,
          onBack = { viewModel.navigateBack() },
          onSelectContact = { viewModel.startChatWithContact(it) },
          onCreateGroup = { name, members -> viewModel.createNewGroup(name, members) },
          onAddContact = { name, phone, email -> viewModel.addNewContact(name, phone, email) }
        )
      }
      Screen.GROUP_INFO -> {
        val currentConv = state.conversations.find { it.id == state.activeConversationId }
          ?: state.conversations.firstOrNull()

        if (currentConv != null) {
          GroupInfoScreen(
            conversation = currentConv,
            currentUser = state.currentUser,
            contacts = state.contacts,
            isDarkMode = state.isDarkMode,
            onBack = { viewModel.navigateBack() }
          )
        } else {
          viewModel.navigateBack()
        }
      }
      Screen.SETTINGS, Screen.PROFILE -> {
        ProfileSettingsScreen(
          currentUser = state.currentUser,
          isDarkMode = state.isDarkMode,
          wallpaper = state.wallpaper,
          readReceiptsEnabled = state.readReceiptsEnabled,
          notificationsEnabled = state.notificationsEnabled,
          vibrationEnabled = state.vibrationEnabled,
          lastSeenPrivacy = state.lastSeenPrivacy,
          onBack = { viewModel.navigateBack() },
          onUpdateProfile = { name, bio, phone, email ->
            viewModel.updateUserProfile(name, bio, phone, email)
          },
          onToggleDarkMode = { viewModel.toggleDarkMode() },
          onSetWallpaper = { viewModel.setWallpaper(it) },
          onToggleReadReceipts = { viewModel.toggleReadReceipts(it) },
          onToggleNotifications = { viewModel.toggleNotifications(it) },
          onToggleVibration = { viewModel.toggleVibration(it) },
          onSetLastSeenPrivacy = { viewModel.setLastSeenPrivacy(it) }
        )
      }
    }
  }
}
