package com.example.cuidadores.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cuidadores.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class MedicamentoReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val medicamentoId = inputData.getLong(KEY_MEDICAMENTO_ID, -1)
        val pacienteNome = inputData.getString(KEY_PACIENTE_NOME) ?: "Paciente"
        val medicamentoNome = inputData.getString(KEY_MEDICAMENTO_NOME) ?: "Medicamento"
        val horario = inputData.getString(KEY_HORARIO) ?: ""
        val dataFim = inputData.getString(KEY_DATA_FIM) // pode ser null

        // Verifica se ainda está no período
        val hoje = LocalDate.now()
        if (dataFim != null && hoje.isAfter(LocalDate.parse(dataFim))) {
            // Não agenda mais
            return Result.success()
        }

        // Dispara a notificação
        showNotification(pacienteNome, medicamentoNome, horario)

        // Agenda o próximo lembrete para o dia seguinte, mesmo horário
        val proximoHorario = LocalTime.parse(horario, DateTimeFormatter.ofPattern("HH:mm"))
        val agora = LocalTime.now()
        val delay = if (agora.isBefore(proximoHorario)) {
            java.time.Duration.between(agora, proximoHorario).toMillis()
        } else {
            java.time.Duration.between(agora, proximoHorario.plusHours(24)).toMillis()
        }
        val nextWork = OneTimeWorkRequestBuilder<MedicamentoReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(nextWork)

        return Result.success()
    }

    private fun showNotification(paciente: String, medicamento: String, horario: String) {
        val channelId = "medicamento_reminder_channel"
        val notificationId = (medicamento + horario + paciente).hashCode()
        val title = "Hora do medicamento"
        val text = "$paciente: tomar $medicamento às $horario"

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lembretes de Medicamento",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_pill_24dp)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
    }

    companion object {
        const val KEY_MEDICAMENTO_ID = "medicamento_id"
        const val KEY_PACIENTE_NOME = "paciente_nome"
        const val KEY_MEDICAMENTO_NOME = "medicamento_nome"
        const val KEY_HORARIO = "horario"
        const val KEY_DATA_FIM = "data_fim"
    }
} 