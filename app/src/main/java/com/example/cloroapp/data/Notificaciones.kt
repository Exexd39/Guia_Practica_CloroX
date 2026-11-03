package com.example.cloroapp.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.cloroapp.MainActivity
import java.util.concurrent.TimeUnit

object Notificador {
    private const val CANAL_ID = "alertas_criticas"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Alertas críticas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos cuando el cloro libre está en nivel crítico"
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
    }

    fun mostrar(context: Context, id: Int, titulo: String, texto: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // Al tocar la notificacion, se abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacion)
    }

    // Revision automatica
    fun programarRevision(context: Context) {
        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val trabajo = PeriodicWorkRequestBuilder<RevisionAlertasWorker>(15, TimeUnit.MINUTES)
            .setConstraints(restricciones)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "revision_alertas",
            ExistingPeriodicWorkPolicy.KEEP,
            trabajo
        )
    }
}

// Lo que Android ejecuta cada 15 minutos en segundo plano
class RevisionAlertasWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Repositorio.iniciar(applicationContext)
        Repositorio.revisarAlertasCriticas(applicationContext)
        return Result.success()
    }
}