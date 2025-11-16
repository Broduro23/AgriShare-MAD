package com.example.semesterproject.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await




class SigninViewModel: ViewModel() {
    private val auth:FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isLoading = mutableStateOf(false)
    val isLoading:State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage:State<String?> = _errorMessage

    private val _userRole = mutableStateOf<String?>(null)
    val userRole:State<String?> = _userRole
    private val _isSignInSucccess = mutableStateOf(false)
    val isSignInSuccess:State<Boolean> = _isSignInSucccess

    fun signIn(email:String,password:String){
        if(email.isBlank()||password.isBlank()){
            _errorMessage.value = "Please enter an email or password"
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        _userRole.value = null
        _isSignInSucccess.value = true

        viewModelScope.launch{
            try{
                val authResult = auth.signInWithEmailAndPassword(email,password).await()
                val user = authResult.user
                if(user!=null){
                    val documentSnapshot = db.collection("users")
                        .document(user.uid)
                        .get()
                        .await()
                    if(documentSnapshot.exists()){
                        val role = documentSnapshot.getString("role")?:"Client"
                        _userRole.value = role
                        _isSignInSucccess.value = true
                        Log.d("SignInViewModel", "Login successful. Role: $role")

                    } else{
                        _errorMessage.value = "User Proflie is missing"
                        auth.signOut()

                    }
                }
            }catch(e: Exception){
                Log.e("SignInViewModel", "Sign in failed", e)
                _errorMessage.value = "Login Failed: ${e.message}"

            }
            finally {
                _isLoading.value = false
            }
        }
    }
    fun resetState(){
        _isLoading.value = false
        _isSignInSucccess.value = false
        _errorMessage.value = null
        _userRole.value = null
    }





}