package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfesorActivity extends AppCompatActivity {

    // Instancias de Firebase para autenticación y base de datos
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    // Texto de bienvenida que se muestra al profesor
    private TextView profText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        // Establecer el título en la barra superior
        setTitle("Bienvenido");

        // Enlazar la vista del texto de bienvenida
        profText = findViewById(R.id.profesorWelcomeText);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Obtener el UID del usuario autenticado actualmente
        String uid = mAuth.getCurrentUser().getUid();

        // Buscar los datos del usuario en la colección "usuarios"
        firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Obtener el nombre del usuario
                    String nombre = documentSnapshot.getString("nombre");

                    // Mostrar mensaje personalizado si tiene nombre
                    if (nombre != null && !nombre.isEmpty()) {
                        profText.setText("Bienvenido " + nombre);
                    } else {
                        profText.setText("Bienvenido/a");
                    }
                })
                .addOnFailureListener(e ->
                        // En caso de error, mostrar mensaje genérico
                        profText.setText("Bienvenido/a"));

        // Mostrar esta pantalla solo por 1.5 segundos antes de redirigir
        new Handler().postDelayed(() -> {
            startActivity(new Intent(ProfesorActivity.this, MainActivity.class));
            finish(); // Finaliza esta actividad para evitar que se vuelva atrás
        }, 1500);
    }
}
