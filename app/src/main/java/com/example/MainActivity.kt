package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.ParijotoBottomNavBar
import com.example.ui.navigation.ParijotoNavGraph
import com.example.ui.theme.ParijotoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParijotoTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { ParijotoBottomNavBar(navController = navController) }
                ) { innerPadding ->
                    ParijotoNavGraph(
                        navController = navController,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}
