package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnReservarSala1, btnReservarSala2, btnReservarEquipo,
            btnEditarReserva, btnCancelarReserva, btnVerReservas, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Panel de Profesor");

        btnReservarSala1 = findViewById(R.id.btnReservarSala1);
        btnReservarSala2 = findViewById(R.id.btnReservarSala2);
        btnReservarEquipo = findViewById(R.id.btnReservarEquipo);
        btnEditarReserva = findViewById(R.id.btnEditarReserva);
        btnCancelarReserva = findViewById(R.id.btnCancelarReserva);
        btnVerReservas = findViewById(R.id.btnVerReservas);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnReservarSala1.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SalaReservaActivity.class)));

        btnReservarSala2.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, Sala2ReservaActivity.class)));

        btnReservarEquipo.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EquipoReservaActivity.class)));

        btnEditarReserva.setOnClickListener(v -> {
            // A futuro: Editar reserva
        });

        btnCancelarReserva.setOnClickListener(v -> {
            // A futuro: Cancelar reserva
        });

        btnVerReservas.setOnClickListener(v -> {
            // A futuro: Ver mis reservas
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
