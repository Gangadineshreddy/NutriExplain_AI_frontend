package com.SIMATS.nutriai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.SIMATS.nutriai.network.ConditionItem
import com.SIMATS.nutriai.network.UserProfileResponse
import com.SIMATS.nutriai.ui.theme.NutriaiTheme
import com.SIMATS.nutriai.ui.theme.PrimaryGreen

@Composable
fun HomeScreen(
    viewModel: NutriViewModel,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val profile = viewModel.userProfile
    val diseases = profile?.conditions ?: emptyList()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(onHistoryClick = onHistoryClick, onProfileClick = onProfileClick)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                shape = CircleShape,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan",
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.2f))
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.userProfile?.profile_image_url != null) {
                            AsyncImage(
                                model = viewModel.userProfile?.profile_image_url,
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Good morning,",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                        Text(
                            text = viewModel.userProfile?.full_name ?: viewModel.userName,
                            color = Color(0xFF111827),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = Color(0xFF111827)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Daily Nutrient Limits
            NutrientLimitsSection(diseases = diseases)

            Spacer(modifier = Modifier.height(40.dp))

            // Welcome Text
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = buildAnnotatedString {
                        append("Welcome to ")
                        withStyle(style = SpanStyle(color = PrimaryGreen)) {
                            append("NutriExplain\nAI")
                        }
                    },
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827),
                    lineHeight = 44.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Scan your food to understand what's inside.",
                    fontSize = 18.sp,
                    color = Color(0xFF6B7280)
                )
            }



            // Nutrition Tips
            Text(
                text = "Nutrition Tips",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    NutritionTipCard(
                        icon = Icons.Outlined.Eco,
                        iconColor = PrimaryGreen,
                        title = "Power of Fiber",
                        description = "Adding 10g of fiber to your breakfast can keep you full until lunch.",
                        backgroundColor = PrimaryGreen.copy(alpha = 0.15f)
                    )
                }
                item {
                    NutritionTipCard(
                        icon = Icons.Outlined.WaterDrop,
                        iconColor = Color(0xFF3B82F6), // Blue
                        title = "Hydration First",
                        description = "Drink a glass of water before each meal to improve digestion and control portions.",
                        backgroundColor = Color(0xFFEFF6FF) // Light Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(150.dp)) // Padding for FAB + Bottom Bar
        }
    }
}


@Composable
fun NutritionTipCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun BottomNavigationBar(onHistoryClick: () -> Unit = {}, onProfileClick: () -> Unit = {}) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF6B7280),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Handle Home */ },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryGreen,
                selectedTextColor = PrimaryGreen,
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistoryClick,
            icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NutriaiTheme {
        HomeScreen(viewModel = NutriViewModel(), onScanClick = {})
    }
}

data class NutrientLimit(
    val name: String,
    val amount: Int,
    val maxAmount: Int,
    val unit: String,
    val description: String
)

fun getNutrientLimitsForDiseases(diseases: List<ConditionItem>): List<NutrientLimit> {
    // Default/baseline limits
    var sugarLimit = 50
    var sugarDesc = "General recommendation"
    
    var sodiumLimit = 2300
    var sodiumDesc = "General recommendation"
    
    var fatLimit = 70
    var fatDesc = "General recommendation"
    
    var fiberLimit = 30
    var fiberDesc = "General recommendation"
    
    var caloriesLimit = 2000
    var caloriesDesc = "General recommendation"

    // Go through each disease and find the strictest (min for limits, max for recommendations)
    for (entry in diseases) {
        when (entry.disease_name) {
            "Diabetes" -> {
                if (25 < sugarLimit) { sugarLimit = 25; sugarDesc = "Diabetes friendly" }
                if (35 > fiberLimit) { fiberLimit = 35; fiberDesc = "Helps manage blood spike" }
            }
            "Hypertension" -> {
                if (1500 < sodiumLimit) { sodiumLimit = 1500; sodiumDesc = "Hypertension friendly" }
            }
            "Heart Disease" -> {
                if (50 < fatLimit) { fatLimit = 50; fatDesc = "Heart Disease friendly" }
                if (1500 < sodiumLimit) { sodiumLimit = 1500; sodiumDesc = "Heart Disease friendly" }
            }
            "Obesity" -> {
                if (1500 < caloriesLimit) { caloriesLimit = 1500; caloriesDesc = "Obesity management" }
                if (30 < sugarLimit) { sugarLimit = 30; sugarDesc = "Helps reduce calories" }
            }
            "Kidney Disease" -> {
                if (1500 < sodiumLimit) { sodiumLimit = 1500; sodiumDesc = "Kidney Disease friendly" }
            }
        }
    }

    return listOf(
        NutrientLimit("Sugar Limit", 0, sugarLimit, "g", sugarDesc),
        NutrientLimit("Salt / Sodium Limit", 0, sodiumLimit, "mg", sodiumDesc),
        NutrientLimit("Fat Limit", 0, fatLimit, "g", fatDesc),
        NutrientLimit("Fiber Recommendation", 0, fiberLimit, "g", fiberDesc),
        NutrientLimit("Calories Limit", 0, caloriesLimit, "kcal", caloriesDesc)
    )
}

@Composable
fun NutrientLimitsSection(diseases: List<ConditionItem>) {
    val limits = remember(diseases) { getNutrientLimitsForDiseases(diseases) }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Daily Nutrient Limits",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(16.dp))

        limits.forEach { limit ->
            NutrientLimitCard(limit = limit)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun NutrientLimitCard(limit: NutrientLimit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = limit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "${limit.maxAmount}${limit.unit}/day",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = limit.description,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}
