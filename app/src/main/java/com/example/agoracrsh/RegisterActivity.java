package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Elementos UI
    private EditText editTextEmail, editTextPassword, editTextName;
    private Spinner roleSpinner;
    private Button registerButton;
    private TextView goToLoginTextView;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registro AgoraCRSH");

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Vincular vistas
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextName = findViewById(R.id.nameEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);

        // Roles disponibles
        String[] roles = {"Docente", "Paradocente", "Coordinador", "Inspector", "PIE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(v -> registrarUsuario());
        goToLoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registrarUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nombre = editTextName.getText().toString().trim();
        String rolSeleccionado = roleSpinner.getSelectedItem().toString().toLowerCase();

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear cuenta
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> datos = new HashMap<>();
                    datos.put("iD", uid);
                    datos.put("nombre", nombre);
                    datos.put("email", email);
                    datos.put("rol", rolSeleccionado);
                    datos.put("aprobado", false); // Esperando aprobación

                    firestore.collection("usuarios").document(uid)
                            .set(datos)
                            .addOnSuccessListener(unused -> {
                                // 🔔 Enviar notificación al admin
                                Map<String, Object> noti = new HashMap<>();
                                noti.put("tipo", "registro");
                                noti.put("titulo", "Nuevo usuario");
                                noti.put("mensaje", nombre + " ha solicitado registrarse.");
                                noti.put("timestamp", System.currentTimeMillis());

                                firestore.collection("notificaciones_admin").add(noti);

                                Toast.makeText(this, "Registro exitoso. Espera aprobación del administrador.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
