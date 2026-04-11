package com.SIMATS.nutriai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.SIMATS.nutriai.ui.theme.NutriaiTheme
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MedicalConditionsScreen(
    viewModel: NutriViewModel,
    onBackClick: () -> Unit, 
    onContinueClick: (List<DiseaseEntry>) -> Unit
) {
    val selectedDiseasesMap = remember { mutableStateMapOf<String, String>() }
    var customDiseaseName by remember { mutableStateOf("") }
    var customDiseaseStage by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val predefinedDiseases = listOf("None", "Diabetes", "Hypertension", "Heart Disease", "Obesity", "Kidney Disease")
    val diseaseStages = listOf("Stage 1", "Stage 2", "Stage 3", "Severe")

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Health Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF111827)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF9FAFB)
                    )
                )
                
                // Progress Bar Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(Color(0xFFF9FAFB))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step 3 of 4",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryGreen
                        )
                        Text(
                            text = "75% Complete",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimaryGreen,
                        trackColor = Color(0xFFE5E7EB)
                    )
                }
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "Any medical conditions?",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827),
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "This helps NutriExplain AI provide personalized nutritional advice tailored to your specific health needs.",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Disease Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Your Disease", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        predefinedDiseases.forEach { disease ->
                            val isSelected = selectedDiseasesMap.containsKey(disease)
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    if (disease == "None") {
                                        selectedDiseasesMap.clear()
                                        selectedDiseasesMap["None"] = ""
                                    } else {
                                        selectedDiseasesMap.remove("None")
                                        if (isSelected) {
                                            selectedDiseasesMap.remove(disease)
                                        } else {
                                            selectedDiseasesMap[disease] = ""
                                        }
                                    }
                                },
                                label = { Text(disease) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircleOutline,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            tint = PrimaryGreen
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                    selectedLabelColor = PrimaryGreen,
                                    selectedLeadingIconColor = PrimaryGreen
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) PrimaryGreen else Color(0xFFE5E7EB),
                                    selectedBorderColor = PrimaryGreen,
                                    borderWidth = if (isSelected) 2.dp else 1.dp
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }
            }

            // Disease Stages (Dynamic)
            if (selectedDiseasesMap.keys.any { it != "None" }) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Disease Stage", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        selectedDiseasesMap.keys.filter { it != "None" }.forEach { disease ->
                            Text("Stage for $disease", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF374151))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val currentStage = selectedDiseasesMap[disease]
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                diseaseStages.forEach { stage ->
                                    val isStageSelected = currentStage == stage
                                    FilterChip(
                                        selected = isStageSelected,
                                        onClick = { selectedDiseasesMap[disease] = stage },
                                        label = { Text(stage) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                            selectedLabelColor = PrimaryGreen
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isStageSelected,
                                            borderColor = if (isStageSelected) PrimaryGreen else Color(0xFFE5E7EB),
                                            selectedBorderColor = PrimaryGreen,
                                            borderWidth = if (isStageSelected) 2.dp else 1.dp
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Other Disease
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Other Disease (Optional)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customDiseaseName,
                        onValueChange = { customDiseaseName = it },
                        placeholder = { Text("Enter disease name", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                    
                    if (customDiseaseName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Stage for ${customDiseaseName}", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF374151))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            diseaseStages.forEach { stage ->
                                val isStageSelected = customDiseaseStage == stage
                                FilterChip(
                                    selected = isStageSelected,
                                    onClick = { customDiseaseStage = stage },
                                    label = { Text(stage) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGreen.copy(alpha = 0.15f),
                                        selectedLabelColor = PrimaryGreen
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isStageSelected,
                                        borderColor = if (isStageSelected) PrimaryGreen else Color(0xFFE5E7EB),
                                        selectedBorderColor = PrimaryGreen,
                                        borderWidth = if (isStageSelected) 2.dp else 1.dp
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = Color(0xFFEF4444), // Red
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color(0xFFEF4444), // Red
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Continue Button
            Button(
                onClick = {
                    validationError = null
                    
                    if (selectedDiseasesMap.isEmpty() && customDiseaseName.isBlank()) {
                        validationError = "Please select at least one disease or 'None'."
                        return@Button
                    }
                    
                    val finalDiseases = mutableListOf<DiseaseEntry>()
                    
                    for ((mDisease, mStage) in selectedDiseasesMap) {
                        if (mDisease != "None" && mStage.isBlank()) {
                            validationError = "Please select a stage for $mDisease."
                            return@Button
                        }
                        if (mDisease != "None") {
                            finalDiseases.add(DiseaseEntry(mDisease, mStage))
                        }
                    }
                    
                    if (customDiseaseName.isNotBlank()) {
                        if (customDiseaseStage.isBlank()) {
                            validationError = "Please select a stage for your custom disease."
                            return@Button
                        }
                        finalDiseases.add(DiseaseEntry(customDiseaseName.trim(), customDiseaseStage))
                    }
                    
                    onContinueClick(finalDiseases)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !viewModel.isLoading,
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You can change these settings at any time in your profile.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicalConditionsPreview() {
    NutriaiTheme {
        MedicalConditionsScreen(viewModel = NutriViewModel(), onBackClick = {}, onContinueClick = {})
    }
}
