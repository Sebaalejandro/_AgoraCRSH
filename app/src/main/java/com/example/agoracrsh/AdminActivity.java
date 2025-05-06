package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView bienvenidaAdminText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Bienvenido");

        bienvenidaAdminText = findViewById(R.id.bienvenidaAdminText);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nombre = documentSnapshot.getString("nombre");

                    if (nombre != null && !nombre.isEmpty()) {
                        bienvenidaAdminText.setText("Bienvenido " + nombre);
                    } else {
                        bienvenidaAdminText.setText("Bienvenido/a Administrador");
                    }
                })
                .addOnFailureListener(e -> bienvenidaAdminText.setText("Bienvenido/a Administrador"));

        new Handler().postDelayed(() -> {
            startActivity(new Intent(AdminActivity.this, AdminMenuActivity.class));
            finish();
        }, 1500); // Solo Durara 1 Segundo Y Medio Esta Pagina
    }
}
