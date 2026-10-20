package com.example.cloroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.ui.theme.CloroAppTheme
import com.example.cloroapp.ui.viewmodel.CloroViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloroAppTheme {
                val viewModel: CloroViewModel = viewModel()
                val granjas by viewModel.granjas.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(granjas) { granja ->
                            Text(text = "${granja.id} - ${granja.nombre}",
                                modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}