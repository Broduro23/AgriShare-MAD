package com.example.semesterproject.pages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.semesterproject.models.Machine
import com.example.semesterproject.viewmodels.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeBookingScreen(
    machine: Machine,
    viewModel: BookingViewModel = viewModel(),
    onBackClick: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    // State for inputs
    var startDateStr by remember { mutableStateOf("") }
    var endDateStr by remember { mutableStateOf("") }

    // ViewModel state
    val isLoading by viewModel.isLoading
    val message by viewModel.message
    val operationSuccess by viewModel.operationSuccess

    // Derived state for calculation
    val totalPrice = remember(startDateStr, endDateStr) {
        calculateTotal(startDateStr, endDateStr, machine.pricePerDay)
    }

    // Handle success
    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            onBookingSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Machine", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D5F3F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- CHANGED: Replaced Column with LazyColumn ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Add padding to the content itself.
                // 'bottom = 100.dp' ensures the last item scrolls ABOVE the fixed button.
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp,
                    bottom = 100.dp
                )
            ) {
                item {
                    // 1. Machine Summary Card
                    BookingMachineSummary(machine)
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    // 2. Date Selection Section
                    Text(
                        text = "Select Dates",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3E2E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Start Date Input
                    DateInputField(
                        label = "Start Date (YYYY-MM-DD)",
                        value = startDateStr,
                        onValueChange = { startDateStr = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // End Date Input
                    DateInputField(
                        label = "End Date (YYYY-MM-DD)",
                        value = endDateStr,
                        onValueChange = { endDateStr = it }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // 3. Price Breakdown
                if (totalPrice > 0) {
                    item {
                        PriceBreakdownCard(
                            pricePerDay = machine.pricePerDay,
                            days = calculateDays(startDateStr, endDateStr),
                            total = totalPrice
                        )
                    }
                }

                // Error Message
                if (message != null && !message!!.contains("success", ignoreCase = true)) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // 4. Confirm Button (Fixed at bottom)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        try {
                            val start = sdf.parse(startDateStr)?.time ?: 0L
                            val end = sdf.parse(endDateStr)?.time ?: 0L

                            viewModel.createBooking(
                                machineId = machine.id,
                                machineName = machine.name,
                                machineImageUrl = machine.imageUrl,
                                ownerId = machine.ownerId,
                                pricePerDay = machine.pricePerDay,
                                startDate = start,
                                endDate = end
                            )
                        } catch (e: Exception) {
                            // Date parse error
                        }
                    },
                    enabled = !isLoading && totalPrice > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D5F3F), // Darker green for primary action
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (totalPrice > 0) "Pay KSh ${String.format("%.0f", totalPrice)}" else "Confirm Booking",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun BookingMachineSummary(machine: Machine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        AsyncImage(
            model = machine.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = machine.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2D3E2E)
            )
            Text(
                text = "KSh ${machine.pricePerDay} / day",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DateInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("e.g. 2023-12-25") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2D5F3F),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
        },
        singleLine = true
    )
}

@Composable
fun PriceBreakdownCard(pricePerDay: Double, days: Long, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Light green bg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Duration", color = Color(0xFF2D3E2E))
                Text("$days Days", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rate", color = Color(0xFF2D3E2E))
                Text("KSh $pricePerDay / day", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFB8E6D5))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D5F3F))
                Text("KSh ${String.format("%.0f", total)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2D5F3F))
            }
        }
    }
}

// --- Helper Functions ---

fun calculateDays(start: String, end: String): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        val s = sdf.parse(start)
        val e = sdf.parse(end)
        if (s != null && e != null && e.after(s)) {
            val diff = e.time - s.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        } else 0
    } catch (e: Exception) { 0 }
}

fun calculateTotal(start: String, end: String, price: Double): Double {
    val days = calculateDays(start, end)
    return if (days > 0) days * price else 0.0
}


