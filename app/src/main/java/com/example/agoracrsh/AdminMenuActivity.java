package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenuActivity extends AppCompatActivity {

    private Button btnSolicitudesSala, btnSolicitudesEquipos, btnInventario, btnCalendario, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        setTitle("Panel Administrador");

        btnSolicitudesSala = findViewById(R.id.btnSolicitudesSala);
        btnSolicitudesEquipos = findViewById(R.id.btnSolicitudesDeEquipos); // <- Única vez
        btnInventario = findViewById(R.id.btnInventario);
        btnCalendario = findViewById(R.id.btnCalendario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnSolicitudesSala.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AdminSolicitudesSalaActivity.class);
            startActivity(intent);
        });

        btnSolicitudesEquipos.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AdminSolicitudesEquipoActivity.class);
            startActivity(intent);
        });

        btnInventario.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, InventarioActivity.class));
        });

        btnCalendario.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, CalendarioActivity.class));
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminMenuActivity.this, LoginActivity.class));
            finish();
        });
    }
}
