package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // Botones del menú principal del profesor
    private Button btnReservarSala1, btnReservarSala2, btnReservarEquipo,
            btnEditarReserva, btnCancelarReserva, btnVerReservas, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Panel de Profesor");

        // Asociar botones con sus respectivos IDs del layout
        btnReservarSala1 = findViewById(R.id.btnReservarSala1);
        btnReservarSala2 = findViewById(R.id.btnReservarSala2);
        btnReservarEquipo = findViewById(R.id.btnReservarEquipo);
        btnEditarReserva = findViewById(R.id.btnEditarReserva);
        btnCancelarReserva = findViewById(R.id.btnCancelarReserva);
        btnVerReservas = findViewById(R.id.btnVerReservas);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Navegar a la pantalla para reservar Sala Enlace 1
        btnReservarSala1.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SalaReservaActivity.class)));

        // Navegar a la pantalla para reservar Sala Enlace 2
        btnReservarSala2.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, Sala2ReservaActivity.class)));

        // Navegar a la pantalla para reservar equipos tecnológicos
        btnReservarEquipo.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EquipoReservaActivity.class)));

        // Funcionalidad futura: editar una reserva existente
        btnEditarReserva.setOnClickListener(v -> {
            // A futuro: Editar reserva
        });

        // Funcionalidad futura: cancelar una reserva existente
        btnCancelarReserva.setOnClickListener(v -> {
            // A futuro: Cancelar reserva
        });

        // Navegar a la pantalla donde el profesor puede ver sus reservas
        btnVerReservas.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MisReservasActivity.class));
        });

        // Cerrar sesión y volver al login
        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Cierra la sesión de Firebase
            startActivity(new Intent(MainActivity.this, LoginActivity.class)); // Redirige al login
            finish(); // Finaliza esta actividad para que no se pueda volver atrás
        });
    }
}
