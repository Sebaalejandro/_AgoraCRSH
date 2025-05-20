package com.example.agoracrsh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InventarioActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Conexión con Firebase Firestore
    private LinearLayout layoutInventario; // Contenedor para mostrar los equipos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        setTitle("Inventario de Equipos");

        db = FirebaseFirestore.getInstance();
        layoutInventario = findViewById(R.id.layoutInventario);

        cargarEquipos(); // Carga los equipos desde Firestore al iniciar la pantalla

        // Configurar botón para agregar nuevos equipos
        Button btnAgregarEquipo = findViewById(R.id.btnAgregarEquipo);
        btnAgregarEquipo.setOnClickListener(v -> mostrarDialogoAgregar());
    }

    // Cargar todos los equipos desde Firebase y mostrarlos como tarjetas
    private void cargarEquipos() {
        layoutInventario.removeAllViews();
        db.collection("inventario")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        String id = doc.getId();
                        String nombre = doc.getString("nombre");
                        Long cantidad = doc.getLong("cantidad");
                        String descripcion = doc.getString("descripcion");

                        // Inflar el diseño de tarjeta
                        View card = getLayoutInflater().inflate(R.layout.inventario_item, null);
                        TextView info = card.findViewById(R.id.txtInfoEquipo);
                        Button btnEditar = card.findViewById(R.id.btnEditarEquipo);
                        Button btnEliminar = card.findViewById(R.id.btnEliminarEquipo);

                        info.setText("Nombre: " + nombre + "\nCantidad: " + cantidad + "\n" + descripcion);

                        // Configurar botones de editar y eliminar
                        btnEditar.setOnClickListener(v -> mostrarDialogoEditar(id, nombre, cantidad, descripcion));
                        btnEliminar.setOnClickListener(v -> confirmarEliminacion(id));

                        layoutInventario.addView(card);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar inventario", Toast.LENGTH_SHORT).show());
    }

    // Muestra un formulario emergente para agregar un nuevo equipo
    private void mostrarDialogoAgregar() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del equipo");
        layout.addView(inputNombre);

        EditText inputCantidad = new EditText(this);
        inputCantidad.setHint("Cantidad disponible");
        layout.addView(inputCantidad);

        EditText inputDescripcion = new EditText(this);
        inputDescripcion.setHint("Descripción");
        layout.addView(inputDescripcion);

        new AlertDialog.Builder(this)
                .setTitle("Agregar Equipo")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = inputNombre.getText().toString().trim();
                    String cantidadStr = inputCantidad.getText().toString().trim();
                    String descripcion = inputDescripcion.getText().toString().trim();

                    if (!nombre.isEmpty() && !cantidadStr.isEmpty()) {
                        int cantidad = Integer.parseInt(cantidadStr);

                        // Guardar nuevo equipo en Firebase
                        db.collection("inventario").add(new Equipo(nombre, cantidad, descripcion))
                                .addOnSuccessListener(ref -> {
                                    Toast.makeText(this, "Equipo agregado", Toast.LENGTH_SHORT).show();
                                    cargarEquipos();
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Muestra un formulario para editar los datos de un equipo existente
    private void mostrarDialogoEditar(String id, String nombre, Long cantidad, String descripcion) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText inputNombre = new EditText(this);
        inputNombre.setText(nombre);
        layout.addView(inputNombre);

        EditText inputCantidad = new EditText(this);
        inputCantidad.setText(String.valueOf(cantidad));
        layout.addView(inputCantidad);

        EditText inputDescripcion = new EditText(this);
        inputDescripcion.setText(descripcion);
        layout.addView(inputDescripcion);

        new AlertDialog.Builder(this)
                .setTitle("Editar Equipo")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = inputNombre.getText().toString().trim();
                    String nuevaCantidadStr = inputCantidad.getText().toString().trim();
                    String nuevaDescripcion = inputDescripcion.getText().toString().trim();

                    if (!nuevoNombre.isEmpty() && !nuevaCantidadStr.isEmpty()) {
                        int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);

                        // Actualizar datos del equipo en Firebase
                        db.collection("inventario").document(id)
                                .update("nombre", nuevoNombre,
                                        "cantidad", nuevaCantidad,
                                        "descripcion", nuevaDescripcion)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Equipo actualizado", Toast.LENGTH_SHORT).show();
                                    cargarEquipos();
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Muestra confirmación antes de eliminar un equipo
    private void confirmarEliminacion(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Equipo")
                .setMessage("¿Estás segurx que deseas eliminar este equipo?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarEquipo(id))
                .setNegativeButton("No", null)
                .show();
    }

    // Elimina un equipo de Firebase
    private void eliminarEquipo(String id) {
        db.collection("inventario").document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Equipo eliminado", Toast.LENGTH_SHORT).show();
                    cargarEquipos();
                });
    }

    // Clase interna para representar un equipo (modelo de datos)
    public static class Equipo {
        public String nombre;
        public int cantidad;
        public String descripcion;

        public Equipo() {}

        public Equipo(String nombre, int cantidad, String descripcion) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.descripcion = descripcion;
        }
    }
}
