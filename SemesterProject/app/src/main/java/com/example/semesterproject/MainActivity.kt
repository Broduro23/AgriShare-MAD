package com.example.semesterproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semesterproject.pages.*
import com.example.semesterproject.models.Machine
import androidx.navigation.*

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
                onLoginSuccess = { role->
                    when(role){
                        "Owner" -> {
                            navController.navigate("home"){
                                popUpTo(route= "landing"){inclusive = false}
                            }
                        }
                        "Operator"->{
                            navController.navigate(route= "home"){
                                popUpTo(route = "landing"){inclusive = false}
                            }
                        }
                        else->{
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
                        onCheckBookingsClick = { navController.navigate("check_bookings/{role}") },
                        onAddMachinesClick = {
                            currentScreen.value = "add"
                        },
                        onAboutClick = { navController.navigate("about") },
                        onLogoutClick = {
                            navController.navigate("landing") {
                                popUpTo("landing") { inclusive = true }
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
                        // Navigate to bookings screen to see the new booking
                        navController.navigate("check_bookings/{role}") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                )
            }
        }

        // Submission Success Page
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