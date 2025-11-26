package com.example.semesterproject.viewmodels


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semesterproject.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


import com.google.firebase.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- UI States ---
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _isSignUpSuccess = mutableStateOf(false)
    val isSignUpSuccess: State<Boolean> = _isSignUpSuccess

    // --- Main Signup Function ---
    // Matches the fields in your SignUpScreen
    fun signUp(

        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String,
        role:String

    ) {
        // 1. Validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields."
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match."
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters."
            return
        }
        if(role.isBlank()){
            _errorMessage.value = "Please select a role"
            return
        }

        // 2. Start Process
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // A. Create Auth User
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // B. Create Firestore User Document
                    val newUser = Users(
                        uid = firebaseUser.uid,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber,
                        role = role

                    )

                    db.collection("users")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()

                    // C. Success
                    Log.d("AuthViewModel", "User created: ${firebaseUser.uid}")
                    _isSignUpSuccess.value = true
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "SignUp failed", e)
                _errorMessage.value = e.message ?: "Registration failed."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _errorMessage.value = null
        _isSignUpSuccess.value = false
        _isLoading.value = false
    }
}