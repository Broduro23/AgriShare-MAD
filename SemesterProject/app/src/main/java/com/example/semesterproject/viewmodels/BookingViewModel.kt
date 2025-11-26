package com.example.semesterproject.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semesterproject.models.Booking
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore


import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // --- State Variables ---
    private val _bookings = mutableStateOf<List<Booking>>(emptyList())
    val bookings: State<List<Booking>> = _bookings

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    private val _operationSuccess = mutableStateOf(false)
    val operationSuccess: State<Boolean> = _operationSuccess

    // --- CLIENT LOGIC: Make a Booking ---
    fun createBooking(
        machineId: String,
        machineName: String,
        machineImageUrl: String,
        ownerId: String, // We keep this param but will verify it against the DB
        pricePerDay: Double,
        startDate: Long,
        endDate: Long
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _message.value = "Error: You must be logged in to book."
            return
        }

        if (startDate >= endDate) {
            _message.value = "Error: End date must be after the start date."
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false
        _message.value = null

        viewModelScope.launch {
            try {
                // 1. Retrieve correct Owner ID from the Machine document directly
                // This ensures we don't rely on potentially empty navigation arguments
                val machineDoc = db.collection("machines").document(machineId).get().await()
                val verifiedOwnerId = machineDoc.getString("ownerId") ?: ownerId

                if (verifiedOwnerId.isEmpty()) {
                    throw Exception("Could not find an owner for this machine.")
                }

                val diff = endDate - startDate
                val days = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
                val total = days * pricePerDay

                val newBookingRef = db.collection("bookings").document()

                val booking = Booking(
                    id = newBookingRef.id,
                    machineId = machineId,
                    machineName = machineName,
                    machineImageUrl = machineImageUrl,
                    clientId = currentUser.uid,
                    clientName = currentUser.displayName ?: currentUser.email ?: "Client",
                    ownerId = verifiedOwnerId, // Use the verified ID from Firestore
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = total,
                    status = "PENDING"
                )

                newBookingRef.set(booking).await()

                _message.value = "Booking request sent successfully!"
                _operationSuccess.value = true

            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error creating booking", e)
                _message.value = "Failed to create booking: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- CLIENT LOGIC: Cancel Booking ---
    fun cancelBooking(bookingId: String) {
        updateBookingStatus(bookingId, "CANCELLED")
    }

    // --- OWNER LOGIC: Approve/Reject ---
    fun approveBooking(bookingId: String) {
        updateBookingStatus(bookingId, "APPROVED")
    }

    fun rejectBooking(bookingId: String) {
        updateBookingStatus(bookingId, "REJECTED")
    }

    // --- SHARED HELPER: Update Status ---
    private fun updateBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                db.collection("bookings").document(bookingId)
                    .update("status", newStatus)
                    .await()

                // Optimistically update the local list
                val updatedList = _bookings.value.map { booking ->
                    if (booking.id == bookingId) {
                        booking.copy(status = newStatus)
                    } else {
                        booking
                    }
                }
                _bookings.value = updatedList

            } catch (e: Exception) {
                _message.value = "Operation failed: ${e.message}"
            }
        }
    }

    // --- DATA FETCHING ---

    // For Clients: "My Bookings"
    fun fetchClientBookings() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val snapshot = db.collection("bookings")
                    .whereEqualTo("clientId", uid)
                    .get()
                    .await()

                _bookings.value = snapshot.toObjects(Booking::class.java)
                    .sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error fetching client bookings", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // For Owners: "Booking Requests" (The function you asked about)
    fun fetchOwnerBookings() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // This query finds all bookings where YOU are listed as the ownerId
                val snapshot = db.collection("bookings")
                    .whereEqualTo("ownerId", uid)
                    .get()
                    .await()

                _bookings.value = snapshot.toObjects(Booking::class.java)
                    .sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error fetching owner bookings", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _message.value = null
        _operationSuccess.value = false
    }
}