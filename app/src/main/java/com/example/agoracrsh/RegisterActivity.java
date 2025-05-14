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

    // Elementos de entrada y UI
    private EditText editTextEmail, editTextPassword, editTextName;
    private Spinner roleSpinner;
    private Button registerButton;
    private TextView goToLoginTextView;

    // Instancias de Firebase para autenticación y base de datos
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Vincular elementos del layout
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextName = findViewById(R.id.nameEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginTextView = findViewById(R.id.goToLoginTextView);

        // Lista de roles disponibles
        String[] roles = {"Docente", "Paradocente", "Coordinador", "Inspector", "PIE"};

        // Adaptador para mostrar los roles en el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, roles);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Acciones de los botones
        registerButton.setOnClickListener(v -> registrarUsuario());

        goToLoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish(); // Cierra la pantalla actual
        });
    }

    // Método que registra al usuario y guarda sus datos en Firestore
    private void registrarUsuario() {
        // Obtener valores ingresados
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nombre = editTextName.getText().toString().trim();
        String rolSeleccionado = roleSpinner.getSelectedItem().toString().toLowerCase();

        // Validar campos vacíos
        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar al usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Crear objeto con los datos del usuario
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("iD", uid);
                    datos.put("nombre", nombre);
                    datos.put("email", email);
                    datos.put("password", password); // (No recomendable guardar contraseñas en texto plano)
                    datos.put("rol", rolSeleccionado);
                    datos.put("aprobado", false); // El administrador debe aprobar al usuario

                    // Guardar en la colección "usuarios"
                    firestore.collection("usuarios").document(uid)
                            .set(datos)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registro exitoso. Espera aprobación del administrador.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish(); // Cierra la pantalla actual
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
