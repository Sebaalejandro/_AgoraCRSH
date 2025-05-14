package com.example.agoracrsh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // Elementos de la interfaz
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterTextView, forgotPasswordTextView;

    // Instancias de Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("AgoraCRSH");

        // Solicitar permiso para mostrar notificaciones (solo Android 13 o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Enlazar vistas
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterTextView = findViewById(R.id.goToRegisterTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Si ya hay un usuario autenticado, comprobar si está aprobado y redirigirlo
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            firestore.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean aprobado = documentSnapshot.getBoolean("aprobado");
                        if (aprobado != null && aprobado) {
                            String rol = documentSnapshot.getString("rol");
                            if ("admin".equals(rol)) {
                                startActivity(new Intent(this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(this, ProfesorActivity.class));
                            }
                            finish();
                        } else {
                            Toast.makeText(this, "Tu cuenta aún no ha sido aprobada por el administrador", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    });
        }

        // Evento botón login
        loginButton.setOnClickListener(v -> loginUser());

        // Redirigir a pantalla de registro
        goToRegisterTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Enviar correo de recuperación si el usuario lo solicita
        forgotPasswordTextView.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Ingresa tu correo para recuperar la contraseña", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Método que valida y realiza el inicio de sesión
    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Verificar campos vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iniciar sesión con Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();
                    firestore.collection("usuarios").document(userId).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Boolean aprobado = documentSnapshot.getBoolean("aprobado");
                                    if (aprobado != null && aprobado) {
                                        String rol = documentSnapshot.getString("rol");
                                        if ("admin".equals(rol)) {
                                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                        } else if ("profesor".equals(rol)
                                                || "para docentes".equals(rol)
                                                || "director".equals(rol)
                                                || "inspector".equals(rol)) {
                                            startActivity(new Intent(LoginActivity.this, ProfesorActivity.class));
                                        } else {
                                            Toast.makeText(this, "Rol no válido", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Tu cuenta aún no ha sido aprobada", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                } else {
                                    Toast.makeText(this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Método que responde a la solicitud de permisos (notificaciones)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificación concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No podrás recibir notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
