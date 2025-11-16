package com.example.semesterproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.semesterproject.pages.*

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

        // Home Page
        composable("home") {
            HomeScreen(
                onMachineClick = { /* optional: open machine details later */ },
                onCheckBookingsClick = { navController.navigate("bookings") },
                onAddMachinesClick = { navController.navigate("addMachine") },
                onAboutClick = { navController.navigate("about") },
                onLogoutClick = {
                    // Go back to landing and clear all previous destinations
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        // Add Machine Page - NOTE: use onSubmitSuccess to match your composable
        composable("addMachine") {
            AddMachinesScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = {
                    // Navigate to submission success (then user can go back to home)
                    navController.navigate("submissionSuccess") {
                        popUpTo("addMachine") { inclusive = false }
                    }
                }
            )
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
