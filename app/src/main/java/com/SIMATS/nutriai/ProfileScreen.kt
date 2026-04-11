package com.SIMATS.nutriai

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.SIMATS.nutriai.network.ConditionItem
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: NutriViewModel,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditClick: () -> Unit = {}, // Original parameter kept for compatibility
    onAddConditionClick: () -> Unit = {} // Original parameter kept for compatibility
) {
    val context = LocalContext.current
    val profile = viewModel.userProfile
    
    // --- EDIT STATES ---
    var isEditingAccount by remember { mutableStateOf(false) }
    var isEditingLifestyle by remember { mutableStateOf(false) }
    var isEditingConditions by remember { mutableStateOf(false) }
    
    // --- FORM DATA ---
    var fullName by remember { mutableStateOf(profile?.full_name ?: viewModel.userName) }
    var email by remember { mutableStateOf(profile?.email ?: viewModel.userEmail) }
    
    var age by remember { mutableStateOf(profile?.age?.toString() ?: "25") }
    var gender by remember { mutableStateOf(profile?.gender ?: "Male") }
    var height by remember { mutableStateOf(profile?.height_cm?.toString() ?: "170") }
    var weight by remember { mutableStateOf(profile?.weight_kg?.toString() ?: "70") }
    var activity by remember { mutableStateOf(profile?.activity_level ?: "Moderate") }
    var sleep by remember { mutableStateOf(profile?.sleep_hours?.toString() ?: "7.5") }
    var stress by remember { mutableStateOf(profile?.stress_level ?: "Medium") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }

    // Sync state when profile loads
    LaunchedEffect(profile) {
        if (profile != null) {
            fullName = profile.full_name ?: viewModel.userName
            email = profile.email ?: viewModel.userEmail
            age = profile.age.toString()
            gender = profile.gender
            height = profile.height_cm.toString()
            weight = profile.weight_kg.toString()
            activity = profile.activity_level
            sleep = profile.sleep_hours.toString()
            stress = profile.stress_level
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            isUploadingImage = true
            viewModel.uploadProfileImage(context, uri) { success ->
                isUploadingImage = false
                if (success) {
                    Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            ProfileBottomNav(onHomeClick = onHomeClick, onHistoryClick = onHistoryClick)
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Header Section
                    item {
                        ProfileHeader(
                            name = if (isEditingAccount) fullName else (profile?.full_name ?: viewModel.userName),
                            email = if (isEditingAccount) email else (profile?.email ?: viewModel.userEmail),
                            imageUrl = selectedImageUri ?: profile?.profile_image_url,
                            isUploading = isUploadingImage,
                            onImageClick = { galleryLauncher.launch("image/*") }
                        )
                    }

                    // Account Details Section
                    item {
                        ProfileSection(
                            title = "Account Details",
                            isEditing = isEditingAccount,
                            onEditToggle = { 
                                if (isEditingAccount) {
                                    // Save logic
                                    saveAllChanges(viewModel, fullName, email, age, gender, height, weight, activity, sleep, stress, context)
                                }
                                isEditingAccount = !isEditingAccount 
                            }
                        ) {
                            if (isEditingAccount) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    InlineEditField(label = "Full Name", value = fullName, onValueChange = { fullName = it }, icon = Icons.Default.Person)
                                    InlineEditField(label = "Email Address", value = email, onValueChange = { email = it }, icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    AccountInfoRow(icon = Icons.Default.Person, label = "Full Name", value = profile?.full_name ?: viewModel.userName)
                                    AccountInfoRow(icon = Icons.Default.Email, label = "Email", value = profile?.email ?: viewModel.userEmail)
                                }
                            }
                        }
                    }

                    // Stats and Health Section
                    item {
                        ProfileSection(
                            title = "Health & Stats",
                            isEditing = isEditingLifestyle,
                            onEditToggle = { 
                                if (isEditingLifestyle) {
                                    saveAllChanges(viewModel, fullName, email, age, gender, height, weight, activity, sleep, stress, context)
                                }
                                isEditingLifestyle = !isEditingLifestyle 
                            }
                        ) {
                            if (isEditingLifestyle) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) { InlineEditField(label = "Age", value = age, onValueChange = { age = it }, icon = Icons.Default.CalendarToday, keyboardType = KeyboardType.Number) }
                                        Box(modifier = Modifier.weight(1f)) { InlineEditField(label = "Gender", value = gender, onValueChange = { gender = it }, icon = Icons.Default.Male) }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Box(modifier = Modifier.weight(1f)) { InlineEditField(label = "Height (cm)", value = height, onValueChange = { height = it }, icon = Icons.Default.Height, keyboardType = KeyboardType.Number) }
                                        Box(modifier = Modifier.weight(1f)) { InlineEditField(label = "Weight (kg)", value = weight, onValueChange = { weight = it }, icon = Icons.Default.MonitorWeight, keyboardType = KeyboardType.Number) }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatCard(label = "Age", value = "${profile?.age ?: 25}", unit = "yrs", icon = Icons.Default.CalendarToday, color = Color(0xFFEFF6FF), tint = Color(0xFF3B82F6))
                                    StatCard(label = "Weight", value = "${profile?.weight_kg ?: 70.0}", unit = "kg", icon = Icons.Default.MonitorWeight, color = Color(0xFFFEF2F2), tint = Color(0xFFEF4444))
                                    StatCard(label = "Height", value = "${profile?.height_cm ?: 170.0}", unit = "cm", icon = Icons.Default.Straighten, color = Color(0xFFECFDF5), tint = PrimaryGreen)
                                }
                            }
                        }
                    }

                    // Lifestyle Habits
                    item {
                        ProfileSection(
                            title = "Lifestyle Habits",
                            isEditing = isEditingLifestyle,
                            onEditToggle = { 
                                if (isEditingLifestyle) {
                                    saveAllChanges(viewModel, fullName, email, age, gender, height, weight, activity, sleep, stress, context)
                                }
                                isEditingLifestyle = !isEditingLifestyle 
                            }
                        ) {
                            if (isEditingLifestyle) {
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    LifestyleChoiceSelector(label = "Activity Level", selected = activity, options = listOf("Sedentary", "Moderate", "High", "Athlete"), onSelect = { activity = it })
                                    InlineEditField(label = "Sleep (Hours)", value = sleep, onValueChange = { sleep = it }, icon = Icons.Default.Nightlight, keyboardType = KeyboardType.Decimal)
                                    LifestyleChoiceSelector(label = "Stress Level", selected = stress, options = listOf("Low", "Medium", "High"), onSelect = { stress = it })
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    HabitCard(icon = Icons.Outlined.FitnessCenter, label = "Activity", value = profile?.activity_level ?: activity, color = Color(0xFFF5F3FF), tint = Color(0xFF8B5CF6))
                                    HabitCard(icon = Icons.Outlined.Nightlight, label = "Sleep", value = "${profile?.sleep_hours ?: sleep} hrs/day", color = Color(0xFFFFF7ED), tint = Color(0xFFF59E0B))
                                    HabitCard(icon = Icons.Outlined.Psychology, label = "Stress", value = profile?.stress_level ?: stress, color = Color(0xFFFDF2F8), tint = Color(0xFFEC4899))
                                }
                            }
                        }
                    }

                    // Health Conditions
                    item {
                        ProfileSection(
                            title = "Health Conditions",
                            isEditing = isEditingConditions,
                            onEditToggle = { isEditingConditions = !isEditingConditions }
                        ) {
                            if (isEditingConditions) {
                                AddConditionRow(
                                    onAdd = { name -> 
                                        viewModel.addCondition(name, "Moderate") { success ->
                                            if (success) Toast.makeText(context, "Condition added", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                diseasesOrDefault(profile?.conditions).forEach { condition ->
                                    ConditionChip(
                                        condition = condition,
                                        isEditing = isEditingConditions,
                                        onRemove = {
                                            viewModel.removeCondition(condition.disease_name, condition.stage) { success ->
                                                if (success) Toast.makeText(context, "Condition removed", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Buttons
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            LogoutButton(onClick = onLogoutClick)
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

                // Universal Save FAB
                androidx.compose.animation.AnimatedVisibility(
                    visible = isEditingAccount || isEditingLifestyle || isEditingConditions,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = {
                            saveAllChanges(viewModel, fullName, email, age, gender, height, weight, activity, sleep, stress, context)
                            isEditingAccount = false
                            isEditingLifestyle = false
                            isEditingConditions = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp).fillMaxWidth(0.9f),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save All Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun saveAllChanges(
    viewModel: NutriViewModel,
    fullName: String,
    email: String,
    age: String,
    gender: String,
    height: String,
    weight: String,
    activity: String,
    sleep: String,
    stress: String,
    context: android.content.Context
) {
    viewModel.updateProfile(
        name = fullName,
        email = email,
        password = null,
        profileImageUrl = viewModel.userProfile?.profile_image_url,
        age = age.toIntOrNull() ?: 25,
        gender = gender,
        height = height.toDoubleOrNull() ?: 170.0,
        weight = weight.toDoubleOrNull() ?: 70.0,
        activity = activity,
        sleep = sleep.toDoubleOrNull() ?: 7.5,
        stress = stress,
        onResult = { success ->
            if (success) {
                Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save profile", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    imageUrl: Any?,
    isUploading: Boolean,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(120.dp)) {
            AsyncImage(
                model = imageUrl ?: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(3.dp, PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                    .clickable { onImageClick() }
            )
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen)
                    .align(Alignment.BottomEnd)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Text(text = email, fontSize = 14.sp, color = Color(0xFF6B7280))
    }
}

@Composable
fun ProfileSection(
    title: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                IconButton(onClick = onEditToggle, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = if (isEditing) PrimaryGreen else Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InlineEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280), modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = Color(0xFFE5E7EB)
            )
        )
    }
}

@Composable
fun AccountInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color(0xFF9CA3AF))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, unit: String, icon: ImageVector, color: Color, tint: Color) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 11.sp, color = tint.copy(alpha = 0.7f))
        Text(text = "$value$unit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = tint)
    }
}

@Composable
fun HabitCard(icon: ImageVector, label: String, value: String, color: Color, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = tint.copy(alpha = 0.7f))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = tint)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LifestyleChoiceSelector(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    Column {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280), modifier = Modifier.padding(bottom = 8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.1f),
                        selectedLabelColor = PrimaryGreen
                    )
                )
            }
        }
    }
}

@Composable
fun ConditionChip(condition: ConditionItem, isEditing: Boolean, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFAEB))
            .border(1.dp, Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = condition.disease_name, color = Color(0xFFD97706), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        if (isEditing) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color(0xFFD97706),
                modifier = Modifier.size(16.dp).clickable { onRemove() }
            )
        }
    }
}

@Composable
fun AddConditionRow(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Search condition...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { if (text.isNotBlank()) { onAdd(text); text = "" } },
            colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryGreen, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEBEE)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
    ) {
        Icon(Icons.Default.Logout, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Log Out", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileBottomNav(onHomeClick: () -> Unit, onHistoryClick: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(selected = false, onClick = onHomeClick, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = onHistoryClick, icon = { Icon(Icons.Outlined.History, null) }, label = { Text("History") })
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Person, null) }, label = { Text("Profile") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen, indicatorColor = Color.Transparent, selectedTextColor = PrimaryGreen))
    }
}

private fun diseasesOrDefault(conditions: List<ConditionItem>?): List<ConditionItem> {
    return conditions ?: emptyList()
}
