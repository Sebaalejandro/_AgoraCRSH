package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnSala, btnProyector, btnEditar, btnCancelar, btnVer, btnCerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("AgoraCRSH - Profesor");

        btnSala = findViewById(R.id.btnReservarSala);
        btnProyector = findViewById(R.id.btnReservarProyector);
        btnEditar = findViewById(R.id.btnEditarReserva);
        btnCancelar = findViewById(R.id.btnCancelarReserva);
        btnVer = findViewById(R.id.btnVerReservas);
        btnCerrar = findViewById(R.id.btnCerrarSesion);

        btnSala.setOnClickListener(v -> {
            // Abre la actividad para reservar sala
            startActivity(new Intent(this, SalaReservaActivity.class));
        });

        btnProyector.setOnClickListener(v -> {
            // Aquí se podrá reservar proyector en el futuro
            // startActivity(new Intent(this, ProyectorReservaActivity.class));
        });

        btnEditar.setOnClickListener(v -> {
            // startActivity(new Intent(this, EditarReservaActivity.class));
        });

        btnCancelar.setOnClickListener(v -> {
            // startActivity(new Intent(this, CancelarReservaActivity.class));
        });

        btnVer.setOnClickListener(v -> {
            // startActivity(new Intent(this, MisReservasActivity.class));
        });

        btnCerrar.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
