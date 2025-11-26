package com.example.semesterproject.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.semesterproject.components.LogoHeader
import com.example.semesterproject.models.Machine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMachineClick: (Machine) -> Unit = {},
    onCheckBookingsClick: () -> Unit = {},
    onAddMachinesClick: () -> Unit = {},
    onOwnerMachinesClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onProfileClick: ()->Unit ={},
    onLogoutClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // State for machines data
    var machines by remember { mutableStateOf<List<Machine>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch machines from Firestore
    LaunchedEffect(Unit) {
        firestore.collection("machines")
            .get()
            .addOnSuccessListener { documents ->
                val machinesList = mutableListOf<Machine>()
                for (document in documents) {
                    try {
                        val machine = Machine(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            machineType = document.getString("machineType") ?: "",
                            description = document.getString("description") ?: "",
                            pricePerDay = document.getDouble("pricePerDay") ?: 0.0,
                            imageUrl = document.getString("imageUrl") ?: "",
                            ownerFirstName = document.getString("ownerFirstName") ?: "",
                            ownerLastName = document.getString("ownerLastName") ?: "",
                            ownerEmail = document.getString("ownerEmail") ?: "",
                            ownerPhone = document.getString("ownerPhone") ?: ""
                        )
                        machinesList.add(machine)
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Error parsing machine: ${e.message}")
                    }
                }
                machines = machinesList
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("HomeScreen", "Error fetching machines: ${e.message}")
                errorMessage = e.message
                isLoading = false
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onCheckBookingsClick = {
                    scope.launch { drawerState.close() }
                    onCheckBookingsClick()
                },
                onAddMachinesClick = {
                    scope.launch { drawerState.close() }
                    onAddMachinesClick()
                },
                onOwnerMachinesClick = {
                    scope.launch { drawerState.close() }
                    onOwnerMachinesClick()
                },
                onAboutClick = {
                    scope.launch { drawerState.close() }
                    onAboutClick()
                },
                onCloseClick = {
                    scope.launch { drawerState.close() }
                },
                onProfileClick= {
                    scope.launch{drawerState.close()}
                    onProfileClick()
                },
                onLogoutClick = {
                    scope.launch {drawerState.close()}
                    onLogoutClick()
                }

            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            LogoHeader()
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open menu",
                                tint = Color(0xFF2D5F3F)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    modifier = Modifier.height(80.dp)
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        // Loading State
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF2D5F3F))
                        }
                    }

                    errorMessage != null -> {
                        // Error State
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error loading machines",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3E2E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    machines.isEmpty() -> {
                        // Empty State
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "🚜",
                                    fontSize = 64.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No machines available yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3E2E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Check back later or add a machine",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    else -> {
                        // Machines List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            items(machines) { machine ->
                                MachineCard(
                                    machine = machine,
                                    onClick = { onMachineClick(machine) }
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
fun DrawerContent(
    onCheckBookingsClick: () -> Unit,
    onAddMachinesClick: () -> Unit,
    onOwnerMachinesClick: () -> Unit,
    onAboutClick: () -> Unit,
    onCloseClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFE8F5E9),
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close drawer",
                        tint = Color(0xFF2D5F3F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Drawer Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, shape = RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌾", fontSize = 30.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "GREENHIRE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D5F3F)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Menu Items
            DrawerMenuItem("Check Bookings", onClick = onCheckBookingsClick)
            Spacer(modifier = Modifier.height(24.dp))
            DrawerMenuItem("Add Machines", onClick = onAddMachinesClick)
            //Spacer(modifier = Modifier.height(24.dp))
            //DrawerMenuItem("Owner Machines", onClick = onOwnerMachinesClick)
            Spacer(modifier = Modifier.height(24.dp))
            DrawerMenuItem("About", onClick = onAboutClick)
            Spacer(modifier = Modifier.height(24.dp))
            DrawerMenuItem("Profile", onClick = onProfileClick)
            Spacer(modifier = Modifier.height(35.dp))
            DrawerMenuItem("Logout", onClick = onLogoutClick)
        }
    }
}

@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF2D3E2E),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    )
}

@Composable
fun MachineCard(machine: Machine, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Machine Image from Supabase
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(machine.imageUrl),
                    contentDescription = machine.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Machine Type Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(
                            color = Color(0xFF2D5F3F).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = machine.machineType,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Machine Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Machine Name
                Text(
                    text = machine.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E2E),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "KSh ${String.format("%.2f", machine.pricePerDay)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D5F3F)
                    )
                    Text(
                        text = " / day",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}