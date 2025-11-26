package com.example.semesterproject.pages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth // Added Import
import com.google.firebase.firestore.FirebaseFirestore
import com.example.semesterproject.models.Machine
import com.example.semesterproject.utils.SupabaseClient
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMachinesScreen(
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance() // Initialize Auth
    val scope = rememberCoroutineScope()

    // Form state variables
    var machineName by remember { mutableStateOf("") }
    var machineType by remember { mutableStateOf("") }
    var machineDescription by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // --- AUTO-POPULATION LOGIC ---
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 1. Auto-fill email from Auth if available
            if (email.isEmpty()) email = currentUser.email ?: ""

            // 2. Fetch Profile Details from Firestore
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Populate fields if they exist in the document
                        // Using safe calls (?.) and elvis operator (?:) to keep existing value or empty string
                        firstName = document.getString("firstName") ?: firstName
                        lastName = document.getString("lastName") ?: lastName
                        phoneNumber = document.getString("phoneNumber") ?: phoneNumber

                        // If email wasn't in Auth, try getting it from Firestore document
                        if (email.isEmpty()) {
                            email = document.getString("email") ?: ""
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AddMachine", "Error fetching user details", e)
                }
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Function to upload image to Supabase
    suspend fun uploadImageToSupabase(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return null
            inputStream?.close()

            val fileName = "machine_${System.currentTimeMillis()}.jpg"

            // Upload to Supabase Storage
            SupabaseClient.storage
                .from("machine-images")
                .upload(fileName, bytes)

            // Get public URL
            val publicUrl = SupabaseClient.storage
                .from("machine-images")
                .publicUrl(fileName)

            Log.d("AddMachine", "Image uploaded to Supabase: $publicUrl")
            publicUrl
        } catch (e: Exception) {
            Log.e("AddMachine", "Supabase upload error: ${e.message}", e)
            null
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                // Image picker section
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Select Photo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedImageUri != null) "Change Photo" else "Select Photo")
                }

                // Show selected image preview
                selectedImageUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input fields
                OutlinedTextField(
                    value = machineName,
                    onValueChange = { machineName = it },
                    label = { Text("Machine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = machineType,
                    onValueChange = { machineType = it },
                    label = { Text("Machine Type (e.g., Tractor, Harvester)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = machineDescription,
                    onValueChange = { machineDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { pricePerDay = it },
                    label = { Text("Price per Day (KSh)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        // Validation
                        val name = machineName.trim()
                        val type = machineType.trim()
                        val desc = machineDescription.trim()
                        val priceStr = pricePerDay.trim()
                        val fName = firstName.trim()
                        val lName = lastName.trim()
                        val mail = email.trim()
                        val phone = phoneNumber.trim()

                        if (name.isEmpty() || type.isEmpty() || desc.isEmpty() ||
                            priceStr.isEmpty() || fName.isEmpty() || lName.isEmpty() ||
                            mail.isEmpty() || phone.isEmpty()
                        ) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (selectedImageUri == null) {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val price = priceStr.toDoubleOrNull()
                        if (price == null || price <= 0) {
                            Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Start upload process
                        isUploading = true

                        scope.launch {
                            try {
                                // Upload image to Supabase
                                val imageUrl = uploadImageToSupabase(selectedImageUri!!)

                                if (imageUrl == null) {
                                    isUploading = false
                                    Toast.makeText(
                                        context,
                                        "Failed to upload image",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }

                                // Save machine data to Firestore with Supabase image URL
                                val machine = Machine(
                                    name = name,
                                    machineType = type,
                                    description = desc,
                                    pricePerDay = price,
                                    imageUrl = imageUrl, // Supabase URL
                                    ownerFirstName = fName,
                                    ownerLastName = lName,
                                    ownerEmail = mail,
                                    ownerPhone = phone,
                                    ownerId = auth.currentUser?.uid ?: "" // Add ownerId to link machine to user
                                )

                                firestore.collection("machines")
                                    .add(machine)
                                    .addOnSuccessListener {
                                        Log.d("AddMachine", "Machine saved to Firestore")
                                        isUploading = false
                                        Toast.makeText(
                                            context,
                                            "Machine added successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onSubmitSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("AddMachine", "Firestore error: ${e.message}")
                                        isUploading = false
                                        Toast.makeText(
                                            context,
                                            "Failed to save: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } catch (e: Exception) {
                                Log.e("AddMachine", "Error: ${e.message}", e)
                                isUploading = false
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5F3F)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...", color = Color.White)
                    } else {
                        Text("Add Machine", color = Color.White)
                    }
                }
            }
        }
    }
}