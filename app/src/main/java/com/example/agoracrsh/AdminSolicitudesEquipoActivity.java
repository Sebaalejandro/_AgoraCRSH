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

    private RecyclerView recyclerView;
    private TextView textoVacio;
    private SolicitudEquipoAdapter adapter;
    private List<Map<String, Object>> listaSolicitudes;
    private List<String> listaIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes_sala); // reutiliza el mismo XML
        setTitle("Solicitudes de Equipos");

        recyclerView = findViewById(R.id.recyclerSolicitudes);
        textoVacio = findViewById(R.id.textoVacio);
        textoVacio.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaSolicitudes = new ArrayList<>();
        listaIds = new ArrayList<>();

        cargarSolicitudesPendientes();
    }

    private void cargarSolicitudesPendientes() {
        FirebaseFirestore.getInstance()
                .collection("reserva_equipo") // CAMBIADO: ahora lee de "reserva_equipo"
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnSuccessListener(query -> {
                    listaSolicitudes.clear();
                    listaIds.clear();

                    for (var doc : query) {
                        listaSolicitudes.add(doc.getData());
                        listaIds.add(doc.getId());
                    }

                    if (listaSolicitudes.isEmpty()) {
                        textoVacio.setVisibility(View.VISIBLE);
                    } else {
                        textoVacio.setVisibility(View.GONE);
                    }

                    adapter = new SolicitudEquipoAdapter(listaSolicitudes, listaIds, this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
    }
}
