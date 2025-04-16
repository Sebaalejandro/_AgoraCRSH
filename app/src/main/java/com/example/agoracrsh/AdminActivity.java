package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Bienvenido");

        TextView bienvenidaText = findViewById(R.id.bienvenidaAdminText);
        bienvenidaText.setText("Bienvenido Administrador");

        new Handler().postDelayed(() -> {
            startActivity(new Intent(AdminActivity.this, AdminMenuActivity.class));
            finish();
        }, 3000); // 3 segundos
    }
}
