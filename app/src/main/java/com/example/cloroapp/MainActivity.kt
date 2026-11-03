package com.example.cloroapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cloroapp.ui.navigation.AppNavigation
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cloroapp.data.Notificador
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.local.AppDatabase
import com.example.cloroapp.ui.theme.CloroAppTheme
import com.example.cloroapp.ui.viewmodel.CloroViewModel
import android.Manifest
class MainActivity : ComponentActivity() {
    private val pedirPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repositorio.iniciar(applicationContext)
        Notificador.crearCanal(this)
        Notificador.programarRevision(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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