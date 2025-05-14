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

    // Instancia de Firestore
    private FirebaseFirestore db;

    // Contenedor donde se mostrarán los usuarios pendientes
    private LinearLayout layoutUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptar_usuarios);
        setTitle("Aprobar Usuarios");

        // Inicializar Firestore y layout
        db = FirebaseFirestore.getInstance();
        layoutUsuarios = findViewById(R.id.layoutUsuarios);

        // Cargar los usuarios que están pendientes de aprobación
        cargarUsuariosPendientes();
    }

    // Método que obtiene los usuarios con el campo "aprobado" en false
    private void cargarUsuariosPendientes() {
        db.collection("usuarios")
                .whereEqualTo("aprobado", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Limpiar el layout antes de cargar nuevos datos
                    layoutUsuarios.removeAllViews();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        // Obtener datos del usuario
                        String uid = doc.getId();
                        String correo = doc.getString("email");
                        String rol = doc.getString("rol");
                        String nombre = doc.getString("nombre");

                        // Inflar la vista personalizada del usuario
                        View card = getLayoutInflater().inflate(R.layout.usuario_card, null);

                        // Referencias a los elementos dentro de la tarjeta
                        TextView infoText = card.findViewById(R.id.solicitudUsuarioInfoTextView);
                        Button btnAceptar = card.findViewById(R.id.btnAceptarUsuario);
                        Button btnRechazar = card.findViewById(R.id.btnRechazarUsuario);

                        // Mostrar los datos del usuario en el TextView
                        infoText.setText("Nombre: " + nombre + "\nCorreo: " + correo + "\nRol: " + rol);

                        // Asignar acciones a los botones
                        btnAceptar.setOnClickListener(v -> aprobarUsuario(uid));
                        btnRechazar.setOnClickListener(v -> rechazarUsuario(uid));

                        // Agregar la tarjeta al layout
                        layoutUsuarios.addView(card);
                    }

                    // Si no hay usuarios pendientes, mostrar mensaje
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No Hay Usuarios Pendientes", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Cargar Usuarios", Toast.LENGTH_SHORT).show());
    }

    // Método que aprueba un usuario cambiando el valor del campo "aprobado" a true
    private void aprobarUsuario(String uid) {
        db.collection("usuarios").document(uid)
                .update("aprobado", true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario Aprobado", Toast.LENGTH_SHORT).show();
                    cargarUsuariosPendientes(); // Recargar la lista
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Aprobar Usuario", Toast.LENGTH_SHORT).show());
    }

    // Método que rechaza un usuario eliminando su documento de Firestore
    private void rechazarUsuario(String uid) {
        db.collection("usuarios").document(uid)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Usuario Rechazado y Eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuariosPendientes(); // Recargar la lista
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error Al Rechazar Usuario", Toast.LENGTH_SHORT).show());
    }
}
