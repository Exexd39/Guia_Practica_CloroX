package com.example.cloroapp.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val CloroColorScheme = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Blanco,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = Blanco,
    onSurface = TextoOscuro,
    secondary = TextoSecundario,
    error = EstadoCritico
)

@Composable
fun CloroAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CloroColorScheme,
        typography = Typography,
        content = content
    )
}