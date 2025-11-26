package com.example.semesterproject.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToSignup: () -> Unit
) {
    // Simple Navigation Logic: Wait 3 seconds, then go to Signup
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds delay
        onNavigateToSignup()
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌾", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GREENHIRE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D5F3F),
                letterSpacing = 2.sp
            )
        }
    }
}