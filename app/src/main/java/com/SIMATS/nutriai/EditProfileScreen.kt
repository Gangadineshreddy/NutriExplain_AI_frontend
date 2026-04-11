package com.SIMATS.nutriai

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    viewModel: NutriViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onManageConditionsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile = viewModel.userProfile

    // Account data
    var fullName by remember { mutableStateOf(profile?.full_name ?: viewModel.userName) }
    var email by remember { mutableStateOf(profile?.email ?: viewModel.userEmail) } 
    var password by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Lifestyle & Health data
    var age by remember { mutableStateOf(profile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(profile?.gender ?: "Male") }
    var height by remember { mutableStateOf(profile?.height_cm?.toString() ?: "") }
    var weight by remember { mutableStateOf(profile?.weight_kg?.toString() ?: "") }
    var activity by remember { mutableStateOf(profile?.activity_level ?: "Moderate") }
    var sleep by remember { mutableStateOf(profile?.sleep_hours?.toString() ?: "") }
    var stress by remember { mutableStateOf(profile?.stress_level ?: "Medium") }

    var isSaving by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with Camera Overlay
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                ) {
                    AsyncImage(
                        model = profileImageUri ?: profile?.profile_image_url ?: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, PrimaryGreen.copy(alpha = 0.5f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen)
                            .align(Alignment.BottomEnd)
                            .clickable { galleryLauncher.launch("image/*") }
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Picture",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // --- ACCOUNT SECTION ---
                SectionDivider("Account details")
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernEditField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { 
                                fullName = it
                                nameError = if (it.isBlank()) "Name cannot be empty" else null
                            },
                            icon = Icons.Default.Person,
                            placeholder = "Enter your full name",
                            errorText = nameError
                        )

                        ModernEditField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { 
                                email = it
                                emailError = if (it.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) "Invalid email address" else null
                            },
                            icon = Icons.Default.Email,
                            placeholder = "Enter your email",
                            keyboardType = KeyboardType.Email,
                            errorText = emailError
                        )

                        ModernEditField(
                            label = "Change Password (Optional)",
                            value = password,
                            onValueChange = { password = it },
                            icon = Icons.Default.Lock,
                            placeholder = "Enter new password",
                            isPassword = true
                        )
                    }
                }

                // --- LIFESTYLE SECTION ---
                SectionDivider("Lifestyle & Health")
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernEditField(
                            label = "Age",
                            value = age,
                            onValueChange = { age = it },
                            icon = Icons.Default.CalendarToday,
                            placeholder = "Enter your age",
                            keyboardType = KeyboardType.Number
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Gender", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Male", "Female", "Other").forEach { option ->
                                    FilterChip(
                                        selected = gender == option,
                                        onClick = { gender = option },
                                        label = { Text(option) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.1f),
                                            selectedLabelColor = PrimaryGreen
                                        )
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModernEditField(label = "Height (cm)", value = height, onValueChange = { height = it }, icon = Icons.Default.Straighten, placeholder = "170", keyboardType = KeyboardType.Number)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ModernEditField(label = "Weight (kg)", value = weight, onValueChange = { weight = it }, icon = Icons.Default.MonitorWeight, placeholder = "70", keyboardType = KeyboardType.Number)
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Activity Level", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 8.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Sedentary", "Moderate", "High", "Athlete").forEach { option ->
                                    FilterChip(
                                        selected = activity == option,
                                        onClick = { activity = option },
                                        label = { Text(option) }
                                    )
                                }
                            }
                        }

                        ModernEditField(
                            label = "Daily Sleep (Hours)",
                            value = sleep,
                            onValueChange = { sleep = it },
                            icon = Icons.Default.Nightlight,
                            placeholder = "7.5",
                            keyboardType = KeyboardType.Decimal
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Stress Level", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Low", "Medium", "High").forEach { option ->
                                    FilterChip(
                                        selected = stress == option,
                                        onClick = { stress = option },
                                        label = { Text(option) }
                                    )
                                }
                            }
                        }
                    }
                }

                // --- HEALTH CONDITIONS SECTION ---
                SectionDivider("Health conditions")
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MedicalInformation,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Manage your diagnosed health conditions to improve AI accuracy.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onManageConditionsClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF374151)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add/Edit Conditions")
                        }
                    }
                }

                // Action Buttons
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            nameError = "Name cannot be empty"
                            return@Button
                        }
                        isSaving = true
                        viewModel.updateProfile(
                            name = fullName,
                            email = if (email != profile?.email) email else null,
                            password = if (password.isNotEmpty()) password else null,
                            profileImageUrl = profileImageUri?.toString() ?: profile?.profile_image_url,
                            age = age.toIntOrNull() ?: profile?.age ?: 25,
                            gender = gender,
                            height = height.toDoubleOrNull() ?: profile?.height_cm ?: 170.0,
                            weight = weight.toDoubleOrNull() ?: profile?.weight_kg ?: 70.0,
                            activity = activity,
                            sleep = sleep.toDoubleOrNull() ?: profile?.sleep_hours ?: 7.5,
                            stress = stress,
                            onResult = { success ->
                                isSaving = false
                                if (success) {
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    onSaveSuccess()
                                } else {
                                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save All Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B7280)),
                    enabled = !isSaving
                ) {
                    Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun SectionDivider(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9CA3AF),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
    }
}

@Composable
fun ModernEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    errorText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFF9CA3AF)) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            singleLine = true,
            isError = errorText != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                errorBorderColor = Color.Red,
                focusedContainerColor = Color(0xFFF9FAFB),
                unfocusedContainerColor = Color(0xFFF9FAFB)
            )
        )
        if (errorText != null) {
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
