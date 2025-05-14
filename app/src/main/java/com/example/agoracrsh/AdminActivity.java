package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    // Instancias para autenticación y base de datos
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    // Texto de bienvenida en la interfaz
    private TextView bienvenidaAdminText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Título de la ventana
        setTitle("Bienvenido");

        // Inicialización de componentes
        bienvenidaAdminText = findViewById(R.id.bienvenidaAdminText);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Obtener UID del usuario autenticado
        String uid = mAuth.getCurrentUser().getUid();

        // Consultar Firestore para obtener el nombre del administrador
        firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nombre = documentSnapshot.getString("nombre");

                    // Mostrar el nombre si existe, si no mostrar mensaje genérico
                    if (nombre != null && !nombre.isEmpty()) {
                        bienvenidaAdminText.setText("Bienvenido " + nombre);
                    } else {
                        bienvenidaAdminText.setText("Bienvenido/a Administrador");
                    }
                })
                .addOnFailureListener(e ->
                        // Si ocurre un error, mostrar mensaje genérico
                        bienvenidaAdminText.setText("Bienvenido/a Administrador")
                );

        // Retraso de 1.5 segundos antes de redirigir al menú principal del administrador
        new Handler().postDelayed(() -> {
            startActivity(new Intent(AdminActivity.this, AdminMenuActivity.class));
            finish(); // Finaliza esta actividad para que no se pueda volver atrás
        }, 1500);
    }
}
