package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMenuActivity extends AppCompatActivity {

    // Botones del menú principal del administrador
    private Button btnSolicitudesSala, btnSolicitudesEquipos, btnInventario, btnCalendario, btnCerrarSesion, btnAceptarUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Título que se muestra en la barra superior
        setTitle("Panel Administrador");

        // Enlazar los botones con sus respectivas vistas del layout
        btnSolicitudesSala = findViewById(R.id.btnSolicitudesSala);
        btnSolicitudesEquipos = findViewById(R.id.btnSolicitudesDeEquipos);
        btnInventario = findViewById(R.id.btnInventario);
        btnCalendario = findViewById(R.id.btnCalendario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAceptarUsers = findViewById(R.id.btnAceptarUsers);

        // Ir a la pantalla de solicitudes de salas
        btnSolicitudesSala.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AdminSolicitudesSalaActivity.class);
            startActivity(intent);
        });

        // Ir a la pantalla de solicitudes de equipos
        btnSolicitudesEquipos.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AdminSolicitudesEquipoActivity.class);
            startActivity(intent);
        });

        // Ir a la pantalla de inventario
        btnInventario.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, InventarioActivity.class);
            startActivity(intent);
        });

        // Ir al calendario semanal del administrador
        btnCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, CalendarioAdminActivity.class);
            startActivity(intent);
        });

        // Ir a la pantalla para aprobar o rechazar usuarios
        btnAceptarUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, AceptarUsuariosActivity.class);
            startActivity(intent);
        });

        // Cerrar sesión y volver a la pantalla de login
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminMenuActivity.this, LoginActivity.class));
            finish();
        });
    }
}
