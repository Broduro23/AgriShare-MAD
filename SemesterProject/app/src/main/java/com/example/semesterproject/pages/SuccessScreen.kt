package com.example.semesterproject.pages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * A reusable success screen for various transactions.
 * (This is now a top-level function, not in a class)
 *
 * @param title The main title (e.g., "Success!", "Booking Confirmed!").
 * @param message A detailed message explaining what happened.
 * @param buttonText The text for the action button (e.g., "Go to Home", "Done").
 * @param autoRedirectMillis The time in milliseconds to wait before automatically redirecting.
 * Set to null to disable auto-redirect.
 * @param onDoneClick The lambda action to perform when the button is clicked OR after the delay.
 */
@Composable
fun SuccessScreen(
    title: String = "Success!",
    message: String,
    buttonText: String = "Done",
    autoRedirectMillis: Long? = null,
    onDoneClick: () -> Unit
) {
    // --- Auto-redirect logic (Restored) ---
    // This LaunchedEffect will run once when the screen appears.
    // If autoRedirectMillis is not null, it will wait for that time
    // and then trigger the onDoneClick action.
    if (autoRedirectMillis != null) {
        LaunchedEffect(Unit) {
            delay(autoRedirectMillis)
            onDoneClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Using the same theme colors from your SignUpScreen
            .background(Color.White)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Success Icon
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Success Icon",
                // Using the green color from your SignUp button
                tint = Color(0xFF7FB89A),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Title Text
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                // Using the dark text color from your SignUp form
                color = Color(0xFF2D3E2E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Dynamic Message Text
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 4. Action Button
            // The user can click this to skip the wait
            Button(
                onClick = onDoneClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7FB89A) // Green theme color
                ),
                shape = RoundedCornerShape(24.dp), // Matching your SignUp button
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// --- PREVIEWS (Also top-level functions) ---

@Preview(showBackground = true, showSystemUi = true, name = "Sign Up Success")
@Composable
fun SignUpSuccessPreview() {
    SuccessScreen(
        title = "Account Created!",
        message = "Your account has been successfully created. You can now log in.",
        buttonText = "Go to Login",
        onDoneClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Booking Success")
@Composable
fun BookingSuccessPreview() {
    SuccessScreen(
        title = "Booking Confirmed!",
        message = "Your payment was successful and the machine is booked.",
        buttonText = "View My Bookings",
        onDoneClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Auto-Redirect Preview")
@Composable
fun AutoRedirectPreview() {
    SuccessScreen(
        title = "Redirecting...",
        message = "You will be redirected in 10 seconds.",
        buttonText = "Go Now",
        autoRedirectMillis = 10000, // 10 seconds
        onDoneClick = {} // <-- This was the missing parameter
    )
}