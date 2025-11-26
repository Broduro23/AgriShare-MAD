package com.example.semesterproject.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semesterproject.models.Booking
import com.example.semesterproject.viewmodels.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckBookingsScreen(
    role: String, // "Owner" or "Client"
    viewModel: BookingViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val bookings by viewModel.bookings
    val isLoading by viewModel.isLoading
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    // Fetch data based on role when screen loads
    LaunchedEffect(role) {
        if (role == "Owner") {
            viewModel.fetchOwnerBookings()
        } else {
            viewModel.fetchClientBookings()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inline logo header
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(25.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🌾", fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "GREENHIRE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D5F3F),
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "RENT. HIRE. FARM.",
                                fontSize = 6.sp,
                                color = Color(0xFF6FA687),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2D5F3F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.height(80.dp)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Add title below the custom header similar to your snippet
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title Section
                Text(
                    text = if (role == "Owner") "Booking Requests" else "My Bookings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E2E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Content Section
                Box(modifier = Modifier.weight(1f)) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF2D5F3F)
                        )
                    } else if (bookings.isEmpty()) {
                        EmptyStateMessage()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(bookings) { booking ->
                                BookingCard(
                                    booking = booking,
                                    role = role,
                                    onCardClick = { selectedBooking = booking },
                                    onApprove = { viewModel.approveBooking(booking.id) },
                                    onReject = { viewModel.rejectBooking(booking.id) },
                                    onCancel = { viewModel.cancelBooking(booking.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show Details Dialog when a booking is selected
        if (selectedBooking != null) {
            BookingDetailDialog(
                booking = selectedBooking!!,
                onDismiss = { selectedBooking = null }
            )
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    role: String,
    onCardClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit
) {
    val statusColor = when (booking.status) {
        "APPROVED" -> Color(0xFF4CAF50) // Green
        "REJECTED" -> Color(0xFFE57373) // Red
        "CANCELLED" -> Color.Gray
        else -> Color(0xFFFFA726) // Orange (Pending)
    }

    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateRange = "${dateFormatter.format(Date(booking.startDate))} - ${dateFormatter.format(Date(booking.endDate))}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Machine Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.machineName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2D2D2D)
                )
                Badge(containerColor = statusColor) {
                    Text(
                        text = booking.status,
                        color = Color.White,
                        modifier = Modifier.padding(4.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info Rows
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = dateRange, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (role == "Owner") "Client: ${booking.clientName}" else "Owner ID: ${booking.ownerId}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Logic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (role == "Owner" && booking.status == "PENDING") {
                    // OWNER ACTIONS
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Reject", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F)),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Approve", fontSize = 12.sp)
                    }
                } else if (role != "Owner" && (booking.status == "PENDING" || booking.status == "APPROVED")) {
                    // CLIENT ACTIONS
                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Cancel Booking", fontSize = 12.sp)
                    }
                } else {
                    // Read Only / Completed
                    Text(
                        text = if(booking.status == "CANCELLED") "Cancelled" else "Action Completed",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun BookingDetailDialog(
    booking: Booking,
    onDismiss: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val start = dateFormatter.format(Date(booking.startDate))
    val end = dateFormatter.format(Date(booking.endDate))

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF2D5F3F),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Booking Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E2E)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Details List
                BookingDetailRow("Machine", booking.machineName)
                BookingDetailRow("Status", booking.status)
                BookingDetailRow("Start Date", start)
                BookingDetailRow("End Date", end)
                BookingDetailRow("Client", booking.clientName)
                BookingDetailRow("Booking ID", booking.id.take(8) + "...") // Truncate ID

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Price", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("KSh ${String.format("%.0f", booking.totalPrice)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D5F3F))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F))
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF2D2D2D))
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Using the style from your snippet for consistency
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No bookings found.",
                fontSize = 16.sp,
                color = Color(0xFF2D3E2E),
                textAlign = TextAlign.Center
            )
        }
    }
}