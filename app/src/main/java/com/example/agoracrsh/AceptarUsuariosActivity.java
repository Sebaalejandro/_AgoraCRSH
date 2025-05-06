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

                        btnAceptar.setOnClickListener(v -> aprobarUsuario(uid));
                        btnRechazar.setOnClickListener(v -> rechazarUsuario(uid));

                        layoutUsuarios.addView(card);
                    }

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No hay usuarios pendientes", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show());
    }

    private void aprobarUsuario(String uid) {
        db.collection("usuarios").document(uid)
                .update("aprobado", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario aprobado", Toast.LENGTH_SHORT).show();
                    cargarUsuariosPendientes();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al aprobar usuario", Toast.LENGTH_SHORT).show());
    }

    private void rechazarUsuario(String uid) {
        db.collection("usuarios").document(uid)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario rechazado y eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuariosPendientes();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al rechazar usuario", Toast.LENGTH_SHORT).show());
    }
}
