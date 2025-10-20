package com.example.project_better_me.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.Home


data class NavItem(val route: String, val icon: @Composable () -> Unit, val label: String)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        NavItem("dashboard", { Icon(Icons.Filled.Home, contentDescription = "Dash") }, "Dash"),
        NavItem("workout", { Icon(Icons.Filled.FitnessCenter, contentDescription = "Workout") }, "Workout"),
        NavItem("diet", { Icon(Icons.Filled.Fastfood, contentDescription = "Diet") }, "Diet"),
        NavItem("sleep", { Icon(Icons.Filled.Bedtime, contentDescription = "Sleep") }, "Sleep"),
        NavItem("more", { Icon(Icons.Filled.MoreHoriz, contentDescription = "More") }, "More")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { item.icon() },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
