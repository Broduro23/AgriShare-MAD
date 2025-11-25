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

    NavHost(
        navController = navController,
        startDestination = "landing"
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
                        onCheckBookingsClick = { navController.navigate("bookings") },
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
                                // Handle booking logic here
                                // For example: navController.navigate("booking/${machineToBook.id}")
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

        // Submission Success Page
        composable("submissionSuccess") {
            SubmissionSuccessScreen(
                onBackClick = { navController.navigate("home") }
            )
        }

        // Check Bookings Page
        composable("bookings") {
            CheckBookingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // About Page (empty placeholder for now)
        composable("about") {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}