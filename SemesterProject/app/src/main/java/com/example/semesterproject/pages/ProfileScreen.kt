package com.example.semesterproject.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semesterproject.models.Booking
import com.example.semesterproject.models.Users // --- CHANGED: Imported Users
import com.example.semesterproject.viewmodels.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val user by viewModel.user
    val isLoading by viewModel.isLoading
    val bookings by viewModel.displayedBookings

    val totalMachines by viewModel.totalMachines
    val totalBookingsCount by viewModel.totalBookingsCount
    val currentFilter by viewModel.currentFilter

    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D5F3F))
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color(0xFF2D5F3F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2D5F3F))
                }
            } else if (user != null) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Profile Header Section
                    item {
                        // CHANGED: Passing 'user' which is of type Users
                        ProfileHeaderCard(
                            user = user!!,
                            onEditClick = { showEditDialog = true }
                        )
                    }

                    // 2. Owner Statistics
                    if (user?.role == "Owner") {
                        item {
                            Text("Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3E2E))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatCard(
                                    label = "Machines",
                                    value = totalMachines.toString(),
                                    icon = Icons.Default.Agriculture,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "Bookings",
                                    value = totalBookingsCount.toString(),
                                    icon = Icons.Default.BookOnline,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 3. Owner Filters
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Filter Bookings", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D3E2E))
                            Spacer(modifier = Modifier.height(8.dp))
                            FilterRow(
                                currentFilter = currentFilter,
                                onFilterSelected = { viewModel.filterBookings(it) }
                            )
                        }
                    }

                    // 4. Bookings List Header
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (user?.role == "Owner") "Booking Requests" else "My Bookings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2D3E2E)
                        )
                    }

                    // 5. The List Items
                    if (bookings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No bookings found.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(bookings) { booking ->
                            ProfileBookingItem(booking = booking, isOwner = user?.role == "Owner")
                        }
                    }
                }
            }
        }

        // Edit Profile Dialog
        if (showEditDialog && user != null) {
            EditProfileDialog(
                user = user!!,
                onDismiss = { showEditDialog = false },
                onSave = { first, last, phone ->
                    viewModel.updateUserProfile(first, last, phone)
                    showEditDialog = false
                }
            )
        }
    }
}

// --- COMPOSABLES ---

@Composable
fun ProfileHeaderCard(
    user: Users, // --- CHANGED: Type is Users
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.firstName.take(1).uppercase() + user.lastName.take(1).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D5F3F)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E2E)
                )
                Text(
                    text = user.role,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.email, fontSize = 14.sp, color = Color(0xFF555555))
                Text(text = user.phoneNumber, fontSize = 14.sp, color = Color(0xFF555555))

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2D5F3F))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Details", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2D5F3F))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3E2E))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FilterRow(currentFilter: String, onFilterSelected: (String) -> Unit) {
    val filters = listOf("All", "PENDING", "APPROVED", "REJECTED", "CANCELLED")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            FilterChip(
                selected = currentFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE8F5E9),
                    selectedLabelColor = Color(0xFF2D5F3F)
                )
            )
        }
    }
}

@Composable
fun ProfileBookingItem(booking: Booking, isOwner: Boolean) {
    val statusColor = when (booking.status) {
        "APPROVED" -> Color(0xFF4CAF50)
        "REJECTED" -> Color(0xFFE57373)
        else -> Color(0xFFFFA726)
    }
    val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(booking.startDate))

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = booking.machineName, fontWeight = FontWeight.Bold, color = Color(0xFF2D2D2D))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = date, fontSize = 12.sp, color = Color.Gray)
                if (isOwner) {
                    Text(text = "Client: ${booking.clientName}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "KSh ${String.format("%.0f", booking.totalPrice)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D5F3F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Badge(containerColor = statusColor) {
                    Text(booking.status, color = Color.White, modifier = Modifier.padding(4.dp), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: Users,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var phone by remember { mutableStateOf(user.phoneNumber) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(firstName, lastName, phone) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F))
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}