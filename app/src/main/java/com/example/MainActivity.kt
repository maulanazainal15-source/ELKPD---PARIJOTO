package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.data.worker.OfflineSyncWorker
import com.example.ui.components.ConnectivityStatusBanner
import com.example.ui.navigation.ParijotoBottomNavBar
import com.example.ui.navigation.ParijotoNavGraph
import com.example.ui.theme.ParijotoTheme
import com.example.util.ConnectivityMonitor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val connectivityMonitor = ConnectivityMonitor(applicationContext)

        setContent {
            ParijotoTheme {
                val isOnline by remember { connectivityMonitor.observeConnectivity() }
                    .collectAsState(initial = connectivityMonitor.isOnline)

                LaunchedEffect(isOnline) {
                    if (isOnline) {
                        OfflineSyncWorker.enqueue(applicationContext)
                    }
                }

                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        ConnectivityStatusBanner(isOnline = isOnline)
                    },
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
