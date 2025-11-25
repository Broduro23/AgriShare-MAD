package com.example.semesterproject.viewmodels
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semesterproject.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class BookingViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- State Variables ---
    // Holds the list of bookings to display in the UI
    private val _bookings = mutableStateOf<List<Booking>>(emptyList())
    val bookings: State<List<Booking>> = _bookings

    // Loading state for spinners
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Message for Toasts/Snackbars (e.g., "Booking Confirmed")
    private val _message = mutableStateOf<String?>(null)
    val message: State<String?> = _message

    // Flag to trigger navigation on success
    private val _operationSuccess = mutableStateOf(false)
    val operationSuccess: State<Boolean> = _operationSuccess

    // --- CLIENT LOGIC: Make a Booking ---
    fun createBooking(
        machineId: String,
        machineName: String,
        machineImageUrl: String,
        ownerId: String,
        pricePerDay: Double,
        startDate: Long,
        endDate: Long
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _message.value = "Error: You must be logged in to book."
            return
        }

        // 1. Validate Dates
        if (startDate >= endDate) {
            _message.value = "Error: End date must be after the start date."
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false
        _message.value = null

        viewModelScope.launch {
            try {
                // 2. Calculate Total Price
                val diff = endDate - startDate
                // Convert millis to days (ensure at least 1 day is charged)
                val days = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
                val total = days * pricePerDay

                // 3. Create Booking Object
                // We generate a new ID from Firestore before setting data
                val newBookingRef = db.collection("bookings").document()

                val booking = Booking(
                    id = newBookingRef.id,
                    machineId = machineId,
                    machineName = machineName,
                    machineImageUrl = machineImageUrl,
                    clientId = currentUser.uid,
                    clientName = currentUser.displayName ?: currentUser.email ?: "Client",
                    ownerId = ownerId, // Important: Links this booking to the specific machine owner
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = total,
                    status = "PENDING" // Default status
                )

                // 4. Save to Firestore
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
        // Only allow cancellation if status is PENDING or APPROVED
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
        _isLoading.value = true
        viewModelScope.launch {
            try {
                db.collection("bookings").document(bookingId)
                    .update("status", newStatus)
                    .await()

                _message.value = "Booking $newStatus"

                // Optimistically update the local list so the UI refreshes instantly
                // without waiting for a network re-fetch
                val updatedList = _bookings.value.map {
                    if (it.id == bookingId) it.copy(status = newStatus) else it
                }
                _bookings.value = updatedList

            } catch (e: Exception) {
                _message.value = "Operation failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- DATA FETCHING ---

    // Used by the Client to see "My Bookings"
    fun fetchClientBookings() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val snapshot = db.collection("bookings")
                    .whereEqualTo("clientId", uid)
                    .get()
                    .await()

                // Convert documents to objects and sort by newest first
                _bookings.value = snapshot.toObjects(Booking::class.java)
                    .sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error fetching client bookings", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Used by the Owner to see "Requests"
    fun fetchOwnerBookings() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
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

    // Reset state when leaving screens
    fun resetState() {
        _message.value = null
        _operationSuccess.value = false
    }

}