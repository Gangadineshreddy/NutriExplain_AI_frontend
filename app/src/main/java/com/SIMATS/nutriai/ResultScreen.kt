package com.SIMATS.nutriai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: NutriViewModel,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nutrition Analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFAFAFA)
                )
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                // --- Image Header ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF477A55)), // Dark green background
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.scannedProduct?.image_url.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Grass,
                                    contentDescription = null,
                                    tint = Color(0xFFA16207),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "SCANNING",
                                    color = Color(0xFFA16207),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    } else {
                        AsyncImage(
                            model = viewModel.scannedProduct!!.image_url,
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

            item {
                // --- Product Title & Metadata ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = viewModel.scannedProduct?.product_name ?: "Unknown Product",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        lineHeight = 32.sp,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "AI: ${viewModel.aiPredictionResult ?: "ANALYZING..."}",
                                color = PrimaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        if (viewModel.foodAnalysisResult != null && viewModel.foodAnalysisResult != "SAFE") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = viewModel.foodAnalysisResult!!,
                                    color = Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${viewModel.scannedProduct?.brand ?: ""} • ${viewModel.scannedProduct?.quantity ?: ""}",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Time",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scanned just now",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                }
            }
            item {
                // --- Professional AI Report Section ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Intelligent Health Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF111827)
                            )
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
                        
                        // AI Prediction Summary
                        AnalysisItem(
                            title = "AI PREDICTION",
                            value = viewModel.aiPredictionResult ?: "Analyzing...",
                            color = if (viewModel.aiPredictionResult == "SAFE") PrimaryGreen else Color(0xFFF59E0B)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Safety Analysis
                        AnalysisItem(
                            title = "SAFETY STATUS",
                            value = viewModel.foodAnalysisResult ?: "Analyzing...",
                            color = if (viewModel.foodAnalysisResult == "SAFE") PrimaryGreen else Color.Red
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Personalized Advice
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF9FAFB))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "PERSONALIZED INSIGHTS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6B7280),
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val recommendations = generatePersonalizedAdvice(viewModel)
                                recommendations.forEach { advice ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(text = "•", color = PrimaryGreen, modifier = Modifier.padding(end = 8.dp))
                                        Text(
                                            text = advice,
                                            fontSize = 14.sp,
                                            color = Color(0xFF374151),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                val product = viewModel.scannedProduct
                // --- Energy & Macronutrients Header ---
                Text(
                    text = "Nutritional Breakdown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF111827)
                )
            }

            item {
                val product = viewModel.scannedProduct
                // --- Energy Details ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PrimaryGreen)
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("ENERGY", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${product?.calories?.toInt() ?: 0} kcal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F8F4))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("PROTEIN", color = PrimaryGreen.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${product?.protein?.toInt() ?: 0}g", color = PrimaryGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                val product = viewModel.scannedProduct
                // --- Macro Grids ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    NutrientMiniCard(modifier = Modifier.weight(1f), title = "CARBS", value = "${product?.carbs?.toInt() ?: 0}g", progress = ((product?.carbs ?: 0.0) / 100).toFloat())
                    NutrientMiniCard(modifier = Modifier.weight(1f), title = "FAT", value = "${product?.fat?.toInt() ?: 0}g", progress = ((product?.fat ?: 0.0) / 70).toFloat())
                    NutrientMiniCard(modifier = Modifier.weight(1f), title = "SUGAR", value = "${product?.sugar?.toInt() ?: 0}g", progress = ((product?.sugar ?: 0.0) / 50).toFloat())
                }
            }

            item {
                // --- Detailed Micronutrients ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Detailed Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailRow("Sodium", "${viewModel.scannedProduct?.sodium?.toInt() ?: 0}mg", "5% DV")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
                        DetailRow("Fiber", "${viewModel.scannedProduct?.fiber?.toInt() ?: 0}g", "12% DV")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AnalysisItem(title: String, value: String, color: Color) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9CA3AF),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, dv: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF4B5563), fontSize = 14.sp)
        Row {
            Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF111827), fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(dv, color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun NutrientMiniCard(modifier: Modifier = Modifier, title: String, value: String, progress: Float) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = Color(0xFF6B7280), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color(0xFF111827), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = PrimaryGreen,
                trackColor = Color(0xFFF3F4F6)
            )
        }
    }
}

fun generatePersonalizedAdvice(viewModel: NutriViewModel): List<String> {
    val advice = mutableListOf<String>()
    val product = viewModel.scannedProduct
    val conditions = viewModel.userProfile?.conditions ?: emptyList()
    val conditionNames = (conditions.map { it.disease_name } + viewModel.onboardingDiseases.map { it.name }).distinct()
    
    if (product == null) return listOf("Analyzing product data...")

    // Condition-specific advice
    if (conditionNames.contains("Diabetes")) {
        if (product.sugar > 10) advice.add("High sugar content detected. This might spike your blood glucose levels.")
        else advice.add("Low sugar content makes this a better choice for managing diabetes.")
    }
    
    if (conditionNames.contains("Hypertension")) {
        if (product.sodium > 400) advice.add("Significant sodium level. Monitor your intake to manage blood pressure.")
        else advice.add("Low sodium profile helps in maintaining healthy blood pressure.")
    }

    if (conditionNames.contains("Heart Disease")) {
        if (product.fat > 15) advice.add("Higher fat content. Consider heart-healthy alternatives.")
    }

    // General Advice based on results
    if (viewModel.foodAnalysisResult == "SAFE") {
        advice.add("This product aligns with your current health profile.")
    } else {
        advice.add("Consider consuming this in moderation due to ${viewModel.foodAnalysisResult?.lowercase()}.")
    }
    
    if (advice.isEmpty()) {
        advice.add("Maintain portion control for balanced nutrition.")
    }
    
    return advice.distinct()
}

@Composable
fun NutrientCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    amount: String,
    progress: Float
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = amount,
                color = Color(0xFF111827),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryGreen,
                trackColor = Color(0xFFF3F4F6)
            )
        }
    }
}
