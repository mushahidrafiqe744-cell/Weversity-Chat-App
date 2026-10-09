package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = WhatsAppGreenLight,
  onPrimary = Color.Black,
  primaryContainer = WhatsAppOutgoingDark,
  onPrimaryContainer = DarkTextPrimary,
  secondary = WhatsAppTeal,
  onSecondary = Color.White,
  tertiary = WhatsAppBlueCheck,
  background = DarkBg,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = Color(0xFF2A3942)
)

private val LightColorScheme = lightColorScheme(
  primary = WhatsAppTeal,
  onPrimary = Color.White,
  primaryContainer = WhatsAppOutgoingLight,
  onPrimaryContainer = LightTextPrimary,
  secondary = WhatsAppGreenLight,
  onSecondary = Color.White,
  tertiary = WhatsAppBlueCheck,
  background = LightBg,
  onBackground = LightTextPrimary,
  surface = Color.White,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurface,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep signature WhatsApp branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
