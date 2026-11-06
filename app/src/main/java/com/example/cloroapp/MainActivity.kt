package com.example.cloroapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.cloroapp.data.Notificador
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.ui.navigation.AppNavigation
import com.example.cloroapp.ui.theme.CloroAppTheme

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
        setContent {
            CloroAppTheme {
                AppNavigation()
            }
        }
    }
}