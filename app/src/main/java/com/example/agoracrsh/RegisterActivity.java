package com.example.agoracrsh;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Spinner roleSpinner;
    private Button registerButton;
    private TextView goToLoginTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Registro - AgoraCRSH");

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);

        registerButton.setOnClickListener(v -> registerUser());

        goToLoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String nombre = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rolSeleccionado = roleSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", userId);
                    userMap.put("nombre", nombre);
                    userMap.put("email", email);
                    userMap.put("password", password);
                    userMap.put("rol", rolSeleccionado);

                    firestore.collection("usuarios")
                            .document(userId)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                if (rolSeleccionado.equals("admin")) {
                                    startActivity(new Intent(this, AdminActivity.class));
                                } else {
                                    startActivity(new Intent(this, ProfesorActivity.class));
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
