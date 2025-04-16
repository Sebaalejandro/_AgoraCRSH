package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfesorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor);

        setTitle("Bienvenido");

        TextView profText = findViewById(R.id.profesorWelcomeText);
        profText.setText("Bienvenido ");

        // Espera 3 segundos y cambia a MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ProfesorActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3000 ms = 3 segundos
    }
}
