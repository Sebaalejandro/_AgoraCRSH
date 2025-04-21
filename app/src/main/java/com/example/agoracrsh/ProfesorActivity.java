package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfesorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView profText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        setTitle("Bienvenido");

        profText = findViewById(R.id.profesorWelcomeText);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nombre = documentSnapshot.getString("nombre");

                    if (nombre != null && !nombre.isEmpty()) {
                        profText.setText("Bienvenido " + nombre);
                    } else {
                        profText.setText("Bienvenido/a");
                    }
                })
                .addOnFailureListener(e -> profText.setText("Bienvenido/a"));

        new Handler().postDelayed(() -> {
            startActivity(new Intent(ProfesorActivity.this, MainActivity.class));
            finish();
        }, 1500); // Solo Durara 1 Segundo Y Medio Esta Pagina xD
    }
}
