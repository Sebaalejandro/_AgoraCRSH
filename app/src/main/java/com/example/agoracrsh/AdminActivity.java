package com.example.agoracrsh;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Panel Administrador");

        TextView adminText = findViewById(R.id.adminWelcomeText);
        adminText.setText("Bienvenido Administrador");
    }
}
