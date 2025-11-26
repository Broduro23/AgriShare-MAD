package com.example.semesterproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semesterproject.pages.*
import com.example.semesterproject.models.Machine
import androidx.navigation.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Shared state to pass machine data between screens
    val selectedMachineForBooking = remember { mutableStateOf<Machine?>(null) }

    // --- FIX 1: Add this variable to store the role after login ---
    val userRole = remember { mutableStateOf("Client") }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Landing Page
        composable("landing") {
            LandingScreen(
                onSignInClick = { navController.navigate("signin") },
                onSignUpClick = { navController.navigate("signup") }
            )
        }

        // Sign In Page
        composable("signin") {
            SignInScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = { role ->
                    // --- FIX 2: Save the role here so we can use it later ---
                    userRole.value = role

                    when(role){
                        "Owner" -> {
                            navController.navigate("home"){
                                popUpTo(route= "landing"){inclusive = false}
                            }
                        }
                        // Removed redundant case logic for clarity, navigating everyone to home
                        else -> {
                            navController.navigate("home") {
                                popUpTo("landing") { inclusive = false }
                            }
                        }
                    }
                }
            )
        }

        // Sign Up Page
        composable("signup") {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("landing") { inclusive = false }
                    }
                }
            )
        }

        // Home Page with integrated navigation
        composable("home") {
            val selectedMachine = remember { mutableStateOf<Machine?>(null) }
            val currentScreen = remember { mutableStateOf("home") }

            when (currentScreen.value) {
                "home" -> {
                    HomeScreen(
                        onMachineClick = { machine ->
                            selectedMachine.value = machine
                            currentScreen.value = "detail"
                        },
                        // --- FIX 3: Use the variable '${userRole.value}' instead of the literal '{role}' ---
                        onCheckBookingsClick = { navController.navigate("check_bookings/${userRole.value}") },
                        onAddMachinesClick = {
                            currentScreen.value = "add"
                        },
                        // Removed onOwnerMachinesClick as it's covered by Profile or CheckBookings
                        onProfileClick = { navController.navigate("profile") },
                        onAboutClick = { navController.navigate("about") },
                        onLogoutClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("landing") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }

                "detail" -> {
                    selectedMachine.value?.let { machine ->
                        MachineDetailScreen(
                            machine = machine,
                            onBackClick = {
                                currentScreen.value = "home"
                                selectedMachine.value = null
                            },
                            onBookClick = { machineToBook ->
                                // Store the machine for booking
                                selectedMachineForBooking.value = machineToBook
                                // Navigate to booking screen
                                navController.navigate("booking")
                            }
                        )
                    }
                }

                "add" -> {
                    AddMachinesScreen(
                        onBackClick = { currentScreen.value = "home" },
                        onSubmitSuccess = { currentScreen.value = "home" }
                    )
                }
            }
        }

        // Booking Screen
        composable("booking") {
            selectedMachineForBooking.value?.let { machine ->
                MakeBookingScreen(
                    machine = machine,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBookingSuccess = {
                        // --- FIX 4: Use '${userRole.value}' here too to prevent crash on 'Pay Now' ---
                        navController.navigate("check_bookings/${userRole.value}") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                )
            }
        }

        // Submission Success Page (Removed duplicate route)
        composable("submissionSuccess") {
            SubmissionSuccessScreen(
                onBackClick = { navController.navigate("home") }
            )
        }

        // Check Bookings Page
        composable(
            route = "check_bookings/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Client"

            CheckBookingsScreen(
                role = role,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen (
                onBackClick = { navController.popBackStack() }
            )
        }

        // Splash Screen
        composable("splash") {
            SplashScreen(
                onNavigateToSignup = {
                    navController.navigate("landing") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // About Page
        composable("about") {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}