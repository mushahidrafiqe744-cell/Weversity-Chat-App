package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.User
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  onSuccessAuth: (User) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isLoginTab by remember { mutableStateOf(true) }
  var name by remember { mutableStateOf("Alex Morgan") }
  var phoneOrEmail by remember { mutableStateOf("+1 (555) 234-8901") }
  var password by remember { mutableStateOf("secret123") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            if (isLoginTab) "Sign In to MyChat" else "Create Account",
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Branding Icon
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(WhatsAppTeal),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Chat,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tab switcher
      TabRow(
        selectedTabIndex = if (isLoginTab) 0 else 1,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = isLoginTab,
          onClick = { isLoginTab = true },
          text = { Text("Log In", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = !isLoginTab,
          onClick = { isLoginTab = false },
          text = { Text("Sign Up", fontWeight = FontWeight.Bold) }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Sign Up Name Field
      if (!isLoginTab) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("auth_name_input")
        )
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Phone or Email Field
      OutlinedTextField(
        value = phoneOrEmail,
        onValueChange = { phoneOrEmail = it },
        label = { Text("Phone Number or Email") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_email_input")
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Password Field
      OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = "Toggle password"
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("auth_password_input")
      )

      errorMessage?.let {
        Spacer(modifier = Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Submit Button
      Button(
        onClick = {
          if (phoneOrEmail.isBlank() || password.isBlank()) {
            errorMessage = "Please enter all required fields"
          } else {
            val authenticatedUser = User(
              id = "user_${name.lowercase().replace(" ", "_")}",
              name = name.ifBlank { "User" },
              phone = phoneOrEmail,
              email = if (phoneOrEmail.contains("@")) phoneOrEmail else "$phoneOrEmail@mychat.io",
              statusBio = "Using MyChat real-time messaging",
              isOnline = true
            )
            onSuccessAuth(authenticatedUser)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("auth_submit_button")
      ) {
        Text(
          text = if (isLoginTab) "Log In" else "Create Account",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(30.dp))

      // Quick Demo Personas
      Text(
        text = "Or quick switch test persona:",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AssistChip(
          onClick = {
            onSuccessAuth(SampleData.currentUser)
          },
          label = { Text("Alex (You)") },
          leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
          modifier = Modifier.weight(1f)
        )
        AssistChip(
          onClick = {
            onSuccessAuth(SampleData.sampleContacts[0])
          },
          label = { Text("Sarah") },
          leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
          modifier = Modifier.weight(1f)
        )
        AssistChip(
          onClick = {
            onSuccessAuth(SampleData.sampleContacts[1])
          },
          label = { Text("David") },
          leadingIcon = { Icon(Icons.Default.Laptop, contentDescription = null) },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}
