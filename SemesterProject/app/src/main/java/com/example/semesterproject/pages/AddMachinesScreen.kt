package com.example.semesterproject.pages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.semesterproject.models.Machine
// --- Data class ---
/*data class Machine(
    val name: String = "",
    val machineType: String = "",
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    val ownerFirstName: String = "",
    val ownerLastName: String = "",
    val ownerEmail: String = "",
    val ownerPhone: String = ""
)*/

// --- Main Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMachinesScreen(
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    // --- Form state variables ---
    var machineName by remember { mutableStateOf("") }
    var machineType by remember { mutableStateOf("") }
    var machineDescription by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF2D5F3F))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.height(80.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Machine",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3E2E),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Form container ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                // --- Input fields ---
                OutlinedTextField(value = machineName, onValueChange = { machineName = it }, label = { Text("Machine Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = machineType, onValueChange = { machineType = it }, label = { Text("Machine Type") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = machineDescription, onValueChange = { machineDescription = it }, label = { Text("Machine Description") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = pricePerDay, onValueChange = { pricePerDay = it }, label = { Text("Price per Day") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = photoPath, onValueChange = { photoPath = it }, label = { Text("Photo URI") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                // --- Submit Button ---
                Button(
                    onClick = {
                        val name = machineName.trim()
                        val type = machineType.trim()
                        val desc = machineDescription.trim()
                        val priceStr = pricePerDay.trim()
                        val photo = photoPath.trim()
                        val fName = firstName.trim()
                        val lName = lastName.trim()
                        val mail = email.trim()
                        val phone = phoneNumber.trim()

                        if (name.isEmpty() || type.isEmpty() || desc.isEmpty() ||
                            priceStr.isEmpty() || photo.isEmpty() || fName.isEmpty() ||
                            lName.isEmpty() || mail.isEmpty() || phone.isEmpty()
                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            Log.d("AddMachine", "Validation failed: Empty fields")
                            return@Button
                        }

                        val price = priceStr.toDoubleOrNull()
                        if (price == null) {
                            Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                            Log.d("AddMachine", "Validation failed: Price not a number")
                            return@Button
                        }

                        val imageRef = storage.reference.child("machine_images/${System.currentTimeMillis()}.jpg")
                        val photoUri = Uri.parse(photo)

                        Log.d("AddMachine", "Starting image upload...")

                        imageRef.putFile(photoUri)
                            .continueWithTask { task ->
                                if (!task.isSuccessful) task.exception?.let { throw it }
                                imageRef.downloadUrl
                            }
                            .addOnSuccessListener { downloadUri ->
                                Log.d("AddMachine", "Image uploaded successfully: $downloadUri")

                                val machine = Machine(
                                    name = name,
                                    machineType = type,
                                    description = desc,
                                    pricePerDay = price,
                                    imageUrl = downloadUri.toString(),
                                    ownerFirstName = fName,
                                    ownerLastName = lName,
                                    ownerEmail = mail,
                                    ownerPhone = phone
                                )

                                firestore.collection("machines")
                                    .add(machine)
                                    .addOnSuccessListener {
                                        Log.d("AddMachine", "Machine saved to Firestore")
                                        Toast.makeText(context, "Machine added successfully", Toast.LENGTH_SHORT).show()
                                        onSubmitSuccess() // ✅ Only navigate after success
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("AddMachine", "Failed to save machine: ${e.message}")
                                        Toast.makeText(context, "Failed to save machine", Toast.LENGTH_SHORT).show()
                                    }

                            }
                            .addOnFailureListener { e ->
                                Log.e("AddMachine", "Image upload failed: ${e.message}")
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add Machine", color = Color.White)
                }
            }
        }
    }
}
