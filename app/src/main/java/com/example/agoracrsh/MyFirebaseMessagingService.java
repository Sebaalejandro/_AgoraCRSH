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

    // ID del canal de notificaciones (se usa en Android 8+)
    private static final String CANAL_ID = "canal_admin";

    // Método que se ejecuta cuando se recibe un mensaje desde FCM
    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        // Si el mensaje contiene una notificación estándar (título y cuerpo)
        if (message.getNotification() != null) {
            String titulo = message.getNotification().getTitle();
            String cuerpo = message.getNotification().getBody();
            mostrarNotificacion(titulo, cuerpo); // Mostrarla como una notificación local
        }

        // Si el mensaje contiene datos personalizados (pueden usarse para lógica extra)
        if (message.getData().size() > 0) {
            Log.d("FCM_DATA", "Datos: " + message.getData()); // Mostrar en log para pruebas
        }
    }

    // Método para construir y mostrar una notificación
    private void mostrarNotificacion(String titulo, String mensaje) {
        crearCanalNotificacion(); // Crear canal (solo si es necesario por versión Android)

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Icono que se muestra en la barra
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridad
                .setAutoCancel(true); // Se cierra sola al hacer clic

        // Mostrar la notificación usando el administrador del sistema
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Método para crear el canal de notificaciones (obligatorio desde Android 8)
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Notificaciones de Solicitudes";
            String descripcion = "Solicitudes de reservas enviadas por docentes";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            // Crear y configurar el canal
            NotificationChannel canal = new NotificationChannel(CANAL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            // Registrar el canal en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }

    // Método que se ejecuta cuando el token del dispositivo es actualizado
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM_TOKEN", "Token actualizado: " + token);
        // Aquí podrías guardar el token en Firestore si necesitas enviarlo al servidor
    }
}
