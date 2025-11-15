package com.example.semesterproject.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semesterproject.viewmodels.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.*
import androidx.compose.foundation.*
import androidx.compose.material.icons.filled.*



// Keep the activity class if you want (not required for navigation but harmless)
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Use default MaterialTheme
            MaterialTheme {
                SignUpScreen(
                    onSignUpClick = { _, _, _, _, _, _ -> },
                    onBackClick = {},
                    onSuccess = {}
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel:AuthViewModel = viewModel(),
    onSignUpClick: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    //Calling the ViewModel Package



    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember{mutableStateOf("")}


    // Observe ViewModel states
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val isSuccess by viewModel.isSignUpSuccess


    if(isSuccess){
        SuccessScreen(
            title = "Account Created!",
            message = "You will be redirected to the homepage in 10 seconds...",
            buttonText = "Go to Home",
            autoRedirectMillis = 10000, // 10 seconds
            onDoneClick = {
                onSuccess() // This triggers the navigation
                viewModel.resetState() // Reset the viewmodel state
            }
        )
    }
    else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Logo Section (this resolves because LogoSection is in same package)
                LogoSection()

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "To Create an Account, Fill the Form Below",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3E2E),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // First Name
                        InputField(
                            label = "First Name",
                            value = firstName,
                            onValueChange = { firstName = it },
                            keyboardType = KeyboardType.Text
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Last Name
                        InputField(
                            label = "Last Name",
                            value = lastName,
                            onValueChange = { lastName = it },
                            keyboardType = KeyboardType.Text
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        InputField(
                            label = "Email",
                            value = email,
                            onValueChange = { email = it },
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phone Number
                        InputField(
                            label = "Phone Number",
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password
                        PasswordInputField(
                            label = "Password",
                            value = password,
                            onValueChange = { password = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password
                        PasswordInputField(
                            label = "Confirm Password",
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DropdownInputField(
                            label = "Select Role",
                            options = listOf("Client", "Owner", "Operator"),
                            selectedOption = role,
                            onOptionSelected = { role = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        // Sign Up Button (call both onSignUpClick and onSuccess)
                        Button(
                            onClick = {
                                // Call the ViewModel function with all current state values
                                viewModel.signUp(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phoneNumber = phoneNumber,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    role = role
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7FB89A)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .width(120.dp)
                        ) {
                            Text(
                                text = "Sign Up",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back button to go to previous screen
                TextButton(onClick = onBackClick) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInputField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3E2E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Use ExposedDropdownMenuBox to create a dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            // This is the TextField that shows the selected value
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {}, // Read-only
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .menuAnchor(), // Anchor the dropdown menu to this text field
                placeholder = { Text("Select a role") },
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        "Dropdown arrow",
                        Modifier.clickable { expanded = !expanded }
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF6FA687),
                    unfocusedBorderColor = Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // This is the actual dropdown menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3E2E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF6FA687),
                unfocusedBorderColor = Color(0xFFCCCCCC)
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3E2E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF6FA687),
                unfocusedBorderColor = Color(0xFFCCCCCC)
            ),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}
