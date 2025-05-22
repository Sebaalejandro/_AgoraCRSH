package com.example.agoracrsh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AceptarUsuariosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout layoutUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptar_usuarios);
        setTitle("Aprobar Usuarios");

        db = FirebaseFirestore.getInstance();
        layoutUsuarios = findViewById(R.id.layoutUsuarios);

        cargarUsuariosPendientes();
    }

    private void cargarUsuariosPendientes() {
        db.collection("usuarios")
                .whereEqualTo("aprobado", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    layoutUsuarios.removeAllViews();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String uid = doc.getId();
                        String correo = doc.getString("email");
                        String rol = doc.getString("rol");
                        String nombre = doc.getString("nombre");

                        View card = getLayoutInflater().inflate(R.layout.usuario_card, null);
                        TextView infoText = card.findViewById(R.id.solicitudUsuarioInfoTextView);
                        Button btnAceptar = card.findViewById(R.id.btnAceptarUsuario);
                        Button btnRechazar = card.findViewById(R.id.btnRechazarUsuario);

                        infoText.setText("Nombre: " + nombre + "\nCorreo: " + correo + "\nRol: " + rol);

                        btnAceptar.setOnClickListener(v -> aprobarUsuario(uid, correo));
                        btnRechazar.setOnClickListener(v -> rechazarUsuario(uid, correo));

                        layoutUsuarios.addView(card);
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No Hay Usuarios Pendientes", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Cargar Usuarios", Toast.LENGTH_SHORT).show());
    }

    private void aprobarUsuario(String uid, String correo) {
        db.collection("usuarios").document(uid)
                .update("aprobado", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario Aprobado", Toast.LENGTH_SHORT).show();
                    enviarNotificacionUsuario(correo, "Registro Aprobado", "Tu cuenta ha sido aprobada por el administrador.");
                    cargarUsuariosPendientes();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Aprobar Usuario", Toast.LENGTH_SHORT).show());
    }

    private void rechazarUsuario(String uid, String correo) {
        enviarNotificacionUsuario(correo, "Registro Rechazado", "Tu solicitud de cuenta fue rechazada. Contacta al administrador si tienes dudas.");

        db.collection("usuarios").document(uid)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario Rechazado y Eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuariosPendientes();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Rechazar Usuario", Toast.LENGTH_SHORT).show());
    }

    private void enviarNotificacionUsuario(String correo, String titulo, String mensaje) {
        Map<String, Object> noti = new HashMap<>();
        noti.put("titulo", titulo);
        noti.put("mensaje", mensaje);
        noti.put("timestamp", System.currentTimeMillis());
        noti.put("usuario", correo);

        db.collection("notificaciones_usuario").add(noti);
    }
}
