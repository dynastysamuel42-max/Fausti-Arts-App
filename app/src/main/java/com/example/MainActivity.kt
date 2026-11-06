package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.FaustiRepository
import com.example.ui.FaustiViewModel
import com.example.ui.FaustiViewModelFactory
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.FaustiArtsTheme
import com.example.util.configureAppCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        // Database and repository initialization
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = FaustiRepository(database.faustiArtsDao())
        val viewModel = FaustiViewModelFactory(repository).create(FaustiViewModel::class.java)

        // Automatically set initial user session (Client Dynasty or Admin) on launch
        lifecycleScope.launch(Dispatchers.IO) {
            val users = database.faustiArtsDao().getAllUsers().firstOrNull()
            if (!users.isNullOrEmpty()) {
                val clientUser = users.find { it.role == "CLIENT" } ?: users.first()
                repository.setCurrentUser(clientUser)
            } else {
                // If database hasn't populated yet, populate initial data
                AppDatabase.populateInitialData(database.faustiArtsDao())
                val seededUsers = database.faustiArtsDao().getAllUsers().firstOrNull()
                val clientUser = seededUsers?.find { it.role == "CLIENT" } ?: seededUsers?.firstOrNull()
                repository.setCurrentUser(clientUser)
            }
        }

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            FaustiArtsTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}
