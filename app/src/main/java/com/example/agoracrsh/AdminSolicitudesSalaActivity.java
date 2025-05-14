package com.example.agoracrsh;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agoracrsh.adapter.SolicitudSalaAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminSolicitudesSalaActivity extends AppCompatActivity {

    // Lista visual donde se mostrarán las solicitudes
    private RecyclerView recyclerView;

    // Adaptador para manejar los datos de las solicitudes
    private SolicitudSalaAdapter adapter;

    // Listas que almacenan los datos y sus IDs
    private List<Map<String, Object>> listaSolicitudes;
    private List<String> listaIds;

    // Texto que aparece si no hay solicitudes pendientes
    private TextView textoVacio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes_sala); // Layout correspondiente a esta actividad
        setTitle("Solicitudes de Sala");

        // Enlazar vistas con elementos del XML
        recyclerView = findViewById(R.id.recyclerSolicitudes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Mostrar en forma de lista vertical

        textoVacio = findViewById(R.id.textoVacio);
        textoVacio.setVisibility(View.GONE); // Ocultar mensaje por defecto

        // Inicializar listas
        listaSolicitudes = new ArrayList<>();
        listaIds = new ArrayList<>();

        // Cargar solicitudes desde Firestore
        cargarSolicitudesPendientes();
    }

    // Método que consulta Firestore para obtener solicitudes de sala con estado "pendiente"
    private void cargarSolicitudesPendientes() {
        FirebaseFirestore.getInstance()
                .collection("reserva_salas") // Colección en Firestore donde están las reservas
                .whereEqualTo("estado", "pendiente") // Solo obtener las que están en estado pendiente
                .get()
                .addOnSuccessListener(query -> {
                    // Limpiar listas anteriores
                    listaSolicitudes.clear();
                    listaIds.clear();

                    // Recorrer los documentos obtenidos y guardar los datos
                    for (var doc : query) {
                        listaSolicitudes.add(doc.getData());
                        listaIds.add(doc.getId());
                    }

                    // Mostrar mensaje si no hay solicitudes
                    if (listaSolicitudes.isEmpty()) {
                        textoVacio.setVisibility(View.VISIBLE);
                    } else {
                        textoVacio.setVisibility(View.GONE);
                    }

                    // Crear adaptador y asignarlo al RecyclerView
                    adapter = new SolicitudSalaAdapter(listaSolicitudes, listaIds, this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show()
                );
    }
}
