package com.example.cloroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cloroapp.data.DatosFalsos
import com.example.cloroapp.ui.navigation.AppNavigation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.local.AppDatabase
import com.example.cloroapp.ui.screen.DashboardScreen
import com.example.cloroapp.ui.theme.CloroAppTheme
import com.example.cloroapp.ui.viewmodel.CloroViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Instanciamos la base de datos y el dao
        val database = AppDatabase.getDatabase(applicationContext)
        val granjaDao = database.granjaDao()

        // Se Crea el Factory para enseñarle al android a como armar el ViewModel
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CloroViewModel(granjaDao) as T
            }
        }

        setContent {
            CloroAppTheme {
                AppNavigation()
            }
        }
    }
}