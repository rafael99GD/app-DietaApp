package com.rafael.dietaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.rafael.dietaapp.navigation.AppNavGraph
import com.rafael.dietaapp.ui.theme.DietaAppTheme
import com.rafael.dietaapp.util.AppTheme
import com.rafael.dietaapp.util.ImportUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as DietaApplication
        val repository = app.repository
        val userPrefs = app.userPreferences

        procesarIntent(intent)

        setContent {
            val theme by userPrefs.theme
            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            DietaAppTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(repository = repository)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        procesarIntent(intent)
    }

    private fun procesarIntent(intent: Intent?) {
        val repository = (application as DietaApplication).repository
        intent?.data?.let { uri ->
            lifecycleScope.launch {
                ImportUtils.importarDesdeUri(this@MainActivity, uri, repository)
            }
        }
    }
}
