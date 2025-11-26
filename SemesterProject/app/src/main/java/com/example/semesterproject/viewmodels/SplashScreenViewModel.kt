package com.example.semesterproject.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SplashViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    // "Owner", "Client", or "SignUp" (not logged in)
    private val _destination = mutableStateOf<String?>(null)
    val destination: State<String?> = _destination

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            // Add a small delay for branding visibility (optional)
            delay(2000)

            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    // Fetch user role from Firestore
                    val snapshot = db.collection("users").document(currentUser.uid).get().await()
                    val role = snapshot.getString("role") ?: "Client"
                    _destination.value = role
                } catch (e: Exception) {
                    // Fallback if fetch fails (e.g., offline)
                    _destination.value = "Client"
                }
            } else {
                // No user logged in, navigate to SignUp
                _destination.value = "SignUp"
            }
            _isLoading.value = false
        }
    }
}