package com.SIMATS.nutriai

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.nutriai.ui.theme.DarkText
import com.SIMATS.nutriai.ui.theme.GrayText
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: NutriViewModel,
    onBackClick: () -> Unit,
    onResetSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Secure Your Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = when(step) {
                    1 -> "Enter your email address to receive a password reset code."
                    2 -> "Enter the 6-digit code sent to your email."
                    else -> "Create a new strong password for your account."
                },
                fontSize = 15.sp,
                color = GrayText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (step == 1) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.sendOtp(email.trim()) { success, err ->
                            if (success) {
                                Toast.makeText(context, "OTP sent to email", Toast.LENGTH_SHORT).show()
                                step = 2
                            } else {
                                Toast.makeText(context, err ?: "Failed to send OTP", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Send OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else if (step == 2) {
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("6-digit OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = PrimaryGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (otp.length != 6) {
                            Toast.makeText(context, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.verifyOtp(email.trim(), otp.trim()) { success, err ->
                            if (success) {
                                Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                                step = 3
                            } else {
                                Toast.makeText(context, err ?: "Invalid OTP", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Verify OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else if (step == 3) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGreen) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = GrayText
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGreen) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = GrayText
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (newPassword.isBlank()) {
                            Toast.makeText(context, "Please enter a new password", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.resetPassword(email.trim(), otp.trim(), newPassword) { success, err ->
                            if (success) {
                                Toast.makeText(context, "Password reset successful!", Toast.LENGTH_LONG).show()
                                onResetSuccess()
                            } else {
                                Toast.makeText(context, err ?: "Reset failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Reset Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            if (viewModel.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
