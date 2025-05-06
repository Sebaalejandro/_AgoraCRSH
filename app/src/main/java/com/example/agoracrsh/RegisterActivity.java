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

    private EditText editTextEmail, editTextPassword, editTextName;
    private Spinner roleSpinner;
    private Button registerButton;
    private TextView goToLoginTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextName = findViewById(R.id.nameEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);

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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("iD", uid);
                    datos.put("nombre", nombre);
                    datos.put("email", email);
                    datos.put("password", password);
                    datos.put("rol", rolSeleccionado);
                    datos.put("aprobado", false); // <- CLAVE

                    firestore.collection("usuarios").document(uid)
                            .set(datos)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registro exitoso. Espera aprobación del administrador.", Toast.LENGTH_SHORT).show();
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
