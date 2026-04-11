package com.SIMATS.nutriai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.nutriai.ui.theme.NutriaiTheme
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthProfileScreen(onContinueClick: (String, String, String, String) -> Unit) {
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("MALE") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Health Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress Bar Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Step 1 of 4",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.25f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryGreen,
                    trackColor = Color(0xFFE5E7EB)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "BASIC INFORMATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Headline
            Text(
                text = "Tell us about yourself",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "This helps NutriExplain AI calculate your personalized nutritional needs and daily caloric goals.",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Age Input
            Text("Age", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF374151))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                placeholder = { Text("e.g. 28", color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gender Selector
            Text("Gender", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF374151))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GenderOption(
                    modifier = Modifier.weight(1f),
                    label = "MALE",
                    icon = Icons.Default.Male,
                    isSelected = selectedGender == "MALE",
                    onClick = { selectedGender = "MALE" }
                )
                GenderOption(
                    modifier = Modifier.weight(1f),
                    label = "FEMALE",
                    icon = Icons.Default.Female,
                    isSelected = selectedGender == "FEMALE",
                    onClick = { selectedGender = "FEMALE" }
                )
                GenderOption(
                    modifier = Modifier.weight(1f),
                    label = "OTHER",
                    icon = Icons.Default.Transgender,
                    isSelected = selectedGender == "OTHER",
                    onClick = { selectedGender = "OTHER" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Height & Weight Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Height (cm)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        placeholder = { Text("175", color = Color(0xFF9CA3AF)) },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Weight (kg)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF374151))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        placeholder = { Text("72", color = Color(0xFF9CA3AF)) },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // No disease selection here anymore
            
            Spacer(modifier = Modifier.height(40.dp))

            // AI Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryGreen.copy(alpha = 0.15f))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = PrimaryGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "AI personalized profiles improve accuracy by 40%",
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            Button(
                onClick = { onContinueClick(age, selectedGender, height, weight) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Save and Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Data is encrypted and used only for analysis.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color(0xFF9CA3AF),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GenderOption(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color.White else Color(0xFFF9FAFB))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryGreen else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryGreen else Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryGreen else Color(0xFF9CA3AF)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthProfilePreview() {
    NutriaiTheme {
        HealthProfileScreen(onContinueClick = { _, _, _, _ -> })
    }
}
