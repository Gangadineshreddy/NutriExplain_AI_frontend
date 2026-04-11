package com.SIMATS.nutriai

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzingScreen(
    viewModel: NutriViewModel,
    onBackClick: () -> Unit, 
    onAnalysisComplete: () -> Unit
) {
    // Animations for the nested circles
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    var simulatedProgress by remember { mutableFloatStateOf(0f) }
    
    // Simulate progress while waiting for real results
    LaunchedEffect(Unit) {
        val duration = 1000L // 1 second
        val startTime = System.currentTimeMillis()
        while (simulatedProgress < 0.9f) {
            val elapsed = System.currentTimeMillis() - startTime
            simulatedProgress = (elapsed.toFloat() / duration).coerceIn(0f, 0.9f)
            kotlinx.coroutines.delay(50)
        }
    }

    // Handle completion
    LaunchedEffect(viewModel.isAnalysisComplete) {
        if (viewModel.isAnalysisComplete) {
            simulatedProgress = 1f
            kotlinx.coroutines.delay(500) // Brief pause at 100%
            onAnalysisComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Analysis",
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
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // --- Animated Nested Circles ---
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9).copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .clip(CircleShape)
                        .background(Color(0xFFC8E6C9).copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.55f)
                        .clip(CircleShape)
                        .border(4.dp, PrimaryGreen, CircleShape)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.45f)
                        .clip(CircleShape)
                        .border(2.dp, PrimaryGreen, CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Science,
                        contentDescription = "Analyzing",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "NEURAL ANALYSIS ACTIVE",
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Processing health\ndata with AI",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- Progress Card ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analysis Progress",
                                color = Color(0xFF374151),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${(simulatedProgress * 100).toInt()}%",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { simulatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PrimaryGreen,
                        trackColor = Color(0xFFF3F4F6),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProgressStep(text = "Scanning product database", isComplete = simulatedProgress > 0.3f)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProgressStep(text = "Analyzing nutritional density", isComplete = simulatedProgress > 0.6f)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProgressStep(text = "Generating AI health prediction", isComplete = viewModel.isAnalysisComplete)
                }
            }
        }
    }

    // Trigger AI Analysis concurrently
    LaunchedEffect(Unit) {
        val product = viewModel.scannedProduct
        if (product != null) {
            val sugar = product.sugar
            val sodium = product.sodium
            val fat = product.fat
            val carbs = product.carbs
            val calories = product.calories
            val protein = product.protein
            val fiber = product.fiber

            // Reset results to show "ANALYZING..." in result screen if needed
            viewModel.aiPredictionResult = null
            viewModel.foodAnalysisResult = null

            // Fire off both requests
            viewModel.aiPredict(sugar, sodium, fat, carbs, calories, protein, fiber)
            viewModel.analyzeFood(sugar, sodium, fat, carbs) { _ -> 
                viewModel.saveScanResult(product.product_name, viewModel.currentBarcode, product.image_url)
            }
        } else {
            viewModel.errorMessage = "No product data found. Please try scanning again."
        }
    }

    // Show error dialog if scan failed
    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null; onBackClick() },
            title = { Text("Analysis Error") },
            text = { Text(viewModel.errorMessage!!) },
            confirmButton = {
                Button(onClick = { viewModel.errorMessage = null; onBackClick() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ProgressStep(text: String, isComplete: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isComplete) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(16.dp)
            )
        } else {
            // Loading spinner placeholder
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = PrimaryGreen,
                strokeWidth = 2.dp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (isComplete) Color(0xFF9CA3AF) else PrimaryGreen,
            fontSize = 14.sp
        )
    }
}
