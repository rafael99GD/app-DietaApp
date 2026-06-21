package com.rafael.dietaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rafael.dietaapp.navigation.AppNavGraph
import com.rafael.dietaapp.ui.theme.DietaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as DietaApplication).repository

        setContent {
            DietaAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(repository = repository)
                }
            }
        }
    }
}
