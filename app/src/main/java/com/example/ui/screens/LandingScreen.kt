package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Screen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppTeal

@Composable
fun LandingScreen(
  isDarkMode: Boolean,
  onGetStarted: () -> Unit,
  onSignIn: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // App Title with WhatsApp-style badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(WhatsAppTeal),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "MyChat",
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          color = WhatsAppTeal
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Generated Hero Illustration
      Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.welcome_hero_1791548412903),
          contentDescription = "Welcome to MyChat",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Welcome to MyChat",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Fast, reliable, real-time messaging inspired by WhatsApp. Stay connected with friends, family, and teams with privacy by default.",
        fontSize = 14.5.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Feature highlights
      FeatureItem(
        icon = Icons.Default.Lock,
        title = "Privacy & Encryption",
        subtitle = "Zero-knowledge encryption for private and group conversations"
      )
      Spacer(modifier = Modifier.height(12.dp))
      FeatureItem(
        icon = Icons.Default.Speed,
        title = "Real-Time Sync",
        subtitle = "Instant message delivery with double checkmarks and typing alerts"
      )
      Spacer(modifier = Modifier.height(12.dp))
      FeatureItem(
        icon = Icons.Default.VolumeUp,
        title = "Voice Notes & Media",
        subtitle = "Record high-quality voice memos and share rich media documents"
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Action buttons
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(
        onClick = onGetStarted,
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("get_started_button")
      ) {
        Text(
          text = "Agree & Continue",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = onSignIn,
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("login_button")
      ) {
        Text(
          text = "Sign In / Switch Account",
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
          color = WhatsAppTeal
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Read our Privacy Policy. Tap \"Agree & Continue\" to accept Terms of Service.",
        fontSize = 11.5.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun FeatureItem(
  icon: ImageVector,
  title: String,
  subtitle: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(WhatsAppTeal.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = WhatsAppTeal,
        modifier = Modifier.size(20.dp)
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = subtitle,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )
    }
  }
}
