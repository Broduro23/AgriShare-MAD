package com.example.semesterproject.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.semesterproject.models.Machine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachineDetailScreen(
    machine: Machine,
    onBackClick: () -> Unit,
    onBookClick: ((Machine) -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2D5F3F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 100.dp)
            ) {
                // Logo Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌾",
                            fontSize = 40.sp
                        )
                        Text(
                            text = "GREENHIRE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D5F3F),
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "RENT YOUR MACHINE",
                            fontSize = 8.sp,
                            color = Color(0xFF2D5F3F),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Machine Image
                if (machine.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = machine.imageUrl,
                        contentDescription = machine.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image Available",
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Description Text
                Text(
                    text = machine.description,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = Color(0xFF2D2D2D),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Machine Details Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        DetailRow(label = "Machine Type", value = machine.machineType)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow(label = "Price per Day", value = "KSh ${String.format("%.2f", machine.pricePerDay)}")
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow(label = "Owner", value = "${machine.ownerFirstName} ${machine.ownerLastName}")
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow(label = "Contact", value = machine.ownerPhone)
                    }
                }
            }

            // Book Now Button - Fixed at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = { onBookClick?.invoke(machine) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB8E6D5),
                        contentColor = Color(0xFF2D5F3F)
                    )
                ) {
                    Text(
                        text = "Book now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color(0xFF2D2D2D),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}