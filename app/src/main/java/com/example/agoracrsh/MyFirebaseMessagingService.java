package com.example.agoracrsh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CANAL_ID = "canal_admin";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        // Notificación tipo texto
        if (message.getNotification() != null) {
            String titulo = message.getNotification().getTitle();
            String cuerpo = message.getNotification().getBody();
            mostrarNotificacion(titulo, cuerpo);
        }

        // Notificación con datos personalizados (opcional)
        if (message.getData().size() > 0) {
            Log.d("FCM_DATA", "Datos: " + message.getData());
        }
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        crearCanalNotificacion(); // Crear canal si es necesario

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Notificaciones de Solicitudes";
            String descripcion = "Solicitudes de reservas enviadas por docentes";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel canal = new NotificationChannel(CANAL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Token actualizado: " + token);
        // Si quieres puedes enviar este token a Firestore para pruebas
    }
}
