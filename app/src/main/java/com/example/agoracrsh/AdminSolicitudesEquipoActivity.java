package com.example.agoracrsh;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agoracrsh.adapter.SolicitudEquipoAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminSolicitudesEquipoActivity extends AppCompatActivity {

    // Vista que muestra la lista de solicitudes
    private RecyclerView recyclerView;

    // Texto que se muestra si no hay solicitudes pendientes
    private TextView textoVacio;

    // Adaptador para el RecyclerView
    private SolicitudEquipoAdapter adapter;

    // Lista de datos de las solicitudes y sus IDs
    private List<Map<String, Object>> listaSolicitudes;
    private List<String> listaIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes_sala); // Se reutiliza el mismo layout de solicitudes de sala

        setTitle("Solicitudes de Equipos");

        // Referencias a las vistas
        recyclerView = findViewById(R.id.recyclerSolicitudes);
        textoVacio = findViewById(R.id.textoVacio);
        textoVacio.setVisibility(View.GONE); // Ocultar el texto vacío por defecto

        // Configurar RecyclerView con un layout vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar listas vacías
        listaSolicitudes = new ArrayList<>();
        listaIds = new ArrayList<>();

        // Cargar solicitudes pendientes desde Firestore
        cargarSolicitudesPendientes();
    }

    // Método que obtiene las solicitudes con estado "pendiente" desde la colección "reserva_equipo"
    private void cargarSolicitudesPendientes() {
        FirebaseFirestore.getInstance()
                .collection("reserva_equipo") // Se accede a la colección de reservas de equipos
                .whereEqualTo("estado", "pendiente") // Solo se obtienen las que están pendientes
                .get()
                .addOnSuccessListener(query -> {
                    listaSolicitudes.clear(); // Limpiar listas por si ya había datos
                    listaIds.clear();

                    // Recorrer cada documento obtenido y agregar sus datos a las listas
                    for (var doc : query) {
                        listaSolicitudes.add(doc.getData());
                        listaIds.add(doc.getId());
                    }

                    // Si la lista está vacía, mostrar mensaje
                    if (listaSolicitudes.isEmpty()) {
                        textoVacio.setVisibility(View.VISIBLE);
                    } else {
                        textoVacio.setVisibility(View.GONE);
                    }

                    // Crear y asignar el adaptador al RecyclerView
                    adapter = new SolicitudEquipoAdapter(listaSolicitudes, listaIds, this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
    }
}
