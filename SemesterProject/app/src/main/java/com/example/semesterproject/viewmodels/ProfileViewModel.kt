package com.example.semesterproject.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semesterproject.models.Booking
import com.example.semesterproject.models.Users
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- User State ---
    private val _user = mutableStateOf<Users?>(null)
    val user: State<Users?> = _user

    // --- Stats State (Owner Only) ---
    private val _totalMachines = mutableStateOf(0)
    val totalMachines: State<Int> = _totalMachines

    private val _totalBookingsCount = mutableStateOf(0)
    val totalBookingsCount: State<Int> = _totalBookingsCount

    // --- Bookings List State ---
    private val _allBookings = mutableStateOf<List<Booking>>(emptyList())
    // The list currently displayed based on filters
    private val _displayedBookings = mutableStateOf<List<Booking>>(emptyList())
    val displayedBookings: State<List<Booking>> = _displayedBookings

    // --- Filter State ---
    private val _currentFilter = mutableStateOf("All")
    val currentFilter: State<String> = _currentFilter

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchProfileData()
    }

    fun fetchProfileData() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 1. Fetch User Details
                val userDoc = db.collection("users").document(uid).get().await()
                val userObj = userDoc.toObject(Users::class.java)
                _user.value = userObj

                if (userObj != null) {
                    if (userObj.role == "Owner") {
                        fetchOwnerData(uid)
                    } else {
                        fetchClientData(uid)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error fetching data", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchOwnerData(uid: String) {
        // 1. Fetch Machines Count
        val machinesSnap = db.collection("machines").whereEqualTo("ownerId", uid).get().await()
        _totalMachines.value = machinesSnap.size()

        // 2. Fetch All Bookings for Owner
        val bookingsSnap = db.collection("bookings").whereEqualTo("ownerId", uid).get().await()
        val bookingsList = bookingsSnap.toObjects(Booking::class.java) ?: emptyList()
        val bookings = bookingsSnap.toObjects(Booking::class.java)

        _totalBookingsCount.value = bookings.size
        _allBookings.value = bookings.sortedByDescending { it.createdAt }

        // Apply current filter
        filterBookings(_currentFilter.value)
    }

    private suspend fun fetchClientData(uid: String) {
        // Fetch bookings made BY this client
        val bookingsSnap = db.collection("bookings").whereEqualTo("clientId", uid).get().await()
        val bookings = bookingsSnap.toObjects(Booking::class.java)

        _allBookings.value = bookings.sortedByDescending { it.createdAt }
        _displayedBookings.value = _allBookings.value
    }

    fun filterBookings(status: String) {
        _currentFilter.value = status
        if (status == "All") {
            _displayedBookings.value = _allBookings.value
        } else {
            _displayedBookings.value = _allBookings.value.filter {
                it.status.equals(status, ignoreCase = true)
            }
        }
    }

    fun updateUserProfile(firstName: String, lastName: String, phone: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "phoneNumber" to phone
                )
                db.collection("users").document(uid).update(updates).await()

                // Refresh local user object
                _user.value = _user.value?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phone
                )
            } catch (e: Exception) {
                Log.e("ProfileVM", "Update failed", e)
            }
        }
    }
}