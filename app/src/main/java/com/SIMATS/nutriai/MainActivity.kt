package com.SIMATS.nutriai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.nutriai.ui.theme.NutriaiTheme

data class DiseaseEntry(
    val name: String,
    val stage: String
)

enum class AppScreen {
    Welcome,
    Onboarding1,
    Onboarding2,
    Onboarding3,
    Login,
    Signup,
    HealthProfile,
    LifestyleHabits,
    MedicalConditions,
    Home,
    Scan,
    Analyzing,
    Result,
    History,
    Profile,
    ForgotPassword
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriaiTheme {
                val viewModel: NutriViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(AppScreen.Welcome) }
                var selectedDiseases by remember { mutableStateOf<List<DiseaseEntry>>(emptyList()) }

                when (currentScreen) {
                    AppScreen.Welcome -> {
                        WelcomeScreen(onLoadingComplete = {
                            currentScreen = AppScreen.Onboarding1
                        })
                    }
                    AppScreen.Onboarding1 -> {
                        OnboardingScreen(
                            onNextClick = { currentScreen = AppScreen.Onboarding2 },
                            onSkipClick = { currentScreen = AppScreen.Login }
                        )
                    }
                    AppScreen.Onboarding2 -> {
                        OnboardingScreen2(
                            onNextClick = { currentScreen = AppScreen.Onboarding3 },
                            onBackClick = { currentScreen = AppScreen.Onboarding1 },
                            onSkipClick = { currentScreen = AppScreen.Login }
                        )
                    }
                    AppScreen.Onboarding3 -> {
                        OnboardingScreen3(
                            onNextClick = { currentScreen = AppScreen.Login },
                            onBackClick = { currentScreen = AppScreen.Onboarding2 },
                            onSkipClick = { currentScreen = AppScreen.Login }
                        )
                    }
                    AppScreen.Login -> {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { currentScreen = AppScreen.Home },
                            onSignupClick = { currentScreen = AppScreen.Signup },
                            onForgotPasswordClick = { currentScreen = AppScreen.ForgotPassword }
                        )
                    }
                    AppScreen.ForgotPassword -> {
                        ForgotPasswordScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = AppScreen.Login },
                            onResetSuccess = { currentScreen = AppScreen.Login }
                        )
                    }
                    AppScreen.Signup -> {
                        SignupScreen(
                            onSignupClick = { name, email, password -> 
                                viewModel.register(name, email, password) {
                                    currentScreen = AppScreen.HealthProfile
                                }
                            },
                            onLoginClick = { currentScreen = AppScreen.Login }
                        )
                    }
                    AppScreen.HealthProfile -> {
                        HealthProfileScreen(
                            onContinueClick = { age, gender, height, weight ->
                                viewModel.onboardingAge = age
                                viewModel.onboardingGender = gender
                                viewModel.onboardingHeight = height
                                viewModel.onboardingWeight = weight
                                currentScreen = AppScreen.LifestyleHabits 
                            }
                        )
                    }
                    AppScreen.LifestyleHabits -> {
                        LifestyleHabitsScreen(
                            onBackClick = { currentScreen = AppScreen.HealthProfile },
                            onSaveClick = { sleep, workoutDays, stress -> 
                                viewModel.onboardingSleep = sleep
                                viewModel.onboardingActivityDays = workoutDays
                                viewModel.onboardingStress = stress
                                currentScreen = AppScreen.MedicalConditions 
                            }
                        )
                    }
                    AppScreen.MedicalConditions -> {
                        MedicalConditionsScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = AppScreen.LifestyleHabits },
                            onContinueClick = { diseases -> 
                                viewModel.onboardingDiseases = diseases
                                viewModel.submitOnboardingProfile(diseases) {
                                    selectedDiseases = diseases
                                    currentScreen = AppScreen.Home 
                                }
                            }
                        )
                    }
                    AppScreen.Home -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onScanClick = { currentScreen = AppScreen.Scan },
                            onHistoryClick = { currentScreen = AppScreen.History },
                            onProfileClick = { currentScreen = AppScreen.Profile }
                        )
                    }
                    AppScreen.Scan -> {
                        ScanScreen(
                            viewModel = viewModel,
                            onCloseClick = { currentScreen = AppScreen.Home },
                            onScanComplete = { currentScreen = AppScreen.Analyzing }
                        )
                    }
                    AppScreen.Analyzing -> {
                        AnalyzingScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = AppScreen.Home },
                            onAnalysisComplete = { currentScreen = AppScreen.Result }
                        )
                    }
                    AppScreen.Result -> {
                        ResultScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = AppScreen.Home }
                        )
                    }
                    AppScreen.History -> {
                        HistoryScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = AppScreen.Home },
                            onHomeClick = { currentScreen = AppScreen.Home },
                            onProfileClick = { currentScreen = AppScreen.Profile },
                            onDetailClick = { currentScreen = AppScreen.Result }
                        )
                    }
                    AppScreen.Profile -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onHomeClick = { currentScreen = AppScreen.Home },
                            onHistoryClick = { currentScreen = AppScreen.History },
                            onLogoutClick = { 
                                viewModel.currentUserId = -1
                                currentScreen = AppScreen.Login 
                            }
                        )
                    }
                }
            }
        }
    }
}
