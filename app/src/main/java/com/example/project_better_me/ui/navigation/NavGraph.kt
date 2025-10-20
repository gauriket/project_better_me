package com.example.project_better_me.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project_better_me.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "dashboard") { // set dashboard as start if you want
        composable("dashboard") { DashboardScreen() }
        composable("workout") { WorkoutScreen() }
        composable("diet") { DietScreen() }
        composable("sleep") { SleepScreen() }
        composable("more") { MoreScreen() }
    }
}
