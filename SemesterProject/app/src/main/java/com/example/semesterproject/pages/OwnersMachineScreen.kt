package com.example.semesterproject.pages



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semesterproject.models.Booking
import com.example.semesterproject.viewmodels.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerMachinesScreen(
    viewModel: BookingViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val bookings by viewModel.bookings
    val isLoading by viewModel.isLoading

    // Group bookings by machine name
    val machineBookings = remember(bookings) {
        bookings.groupBy { it.machineName }
    }

    // Fetch owner bookings when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchOwnerBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title Section
                Text(
                    text = "My Machines & Bookings",
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
                    } else if (machineBookings.isEmpty()) {
                        EmptyMachinesMessage()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(machineBookings.keys.toList()) { machineName ->
                                MachineBookingsCard(
                                    machineName = machineName,
                                    bookings = machineBookings[machineName] ?: emptyList(),
                                    onApprove = { bookingId -> viewModel.approveBooking(bookingId) },
                                    onReject = { bookingId -> viewModel.rejectBooking(bookingId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MachineBookingsCard(
    machineName: String,
    bookings: List<Booking>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val pendingCount = bookings.count { it.status == "PENDING" }
    val approvedCount = bookings.count { it.status == "APPROVED" }
    val totalBookings = bookings.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Machine Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = machineName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2D2D2D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalBookings total booking${if (totalBookings != 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status badges
                    if (pendingCount > 0) {
                        Badge(containerColor = Color(0xFFFFA726)) {
                            Text(
                                text = "$pendingCount pending",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (approvedCount > 0) {
                        Badge(containerColor = Color(0xFF4CAF50)) {
                            Text(
                                text = "$approvedCount active",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color(0xFF2D5F3F),
                        modifier = Modifier
                            .size(24.dp)
                            .then(
                                if (expanded) Modifier else Modifier
                            )
                    )
                }
            }

            // Expanded Bookings List
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(12.dp))

                bookings.forEachIndexed { index, booking ->
                    BookingItemRow(
                        booking = booking,
                        onApprove = { onApprove(booking.id) },
                        onReject = { onReject(booking.id) }
                    )
                    if (index < bookings.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemRow(
    booking: Booking,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (booking.status) {
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFE57373)
        "CANCELLED" -> Color.Gray
        else -> Color(0xFFFFA726)
    }

    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateRange = "${dateFormatter.format(Date(booking.startDate))} - ${dateFormatter.format(Date(booking.endDate))}"

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = Color(0xFF2D5F3F))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.clientName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF2D2D2D)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = dateRange, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "KSh ${String.format("%.0f", booking.totalPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D5F3F)
                )
            }

            Badge(containerColor = statusColor) {
                Text(
                    text = booking.status,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp
                )
            }
        }

        // Action Buttons for Pending Bookings
        if (booking.status == "PENDING") {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Reject", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Approve", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyMachinesMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🚜",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No machines with bookings yet.",
                    fontSize = 16.sp,
                    color = Color(0xFF2D3E2E),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When clients book your machines, they'll appear here.",
                    fontSize = 12.sp,
                    color = Color(0xFF6FA687),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}