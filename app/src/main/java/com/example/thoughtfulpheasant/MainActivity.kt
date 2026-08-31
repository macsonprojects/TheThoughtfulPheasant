package com.example.thoughtfulpheasant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thoughtfulpheasant.ui.EditorScreen
import com.example.thoughtfulpheasant.ui.GeneratorScreen
import com.example.thoughtfulpheasant.ui.theme.ThoughtfulPheasantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThoughtfulPheasantTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavHost(navController = navController, startDestination = "generator") {
                        composable("generator") {
                            GeneratorScreen(
                                onNavigateToEditor = { navController.navigate("editor") }
                            )
                        }
                        composable("editor") {
                            EditorScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}