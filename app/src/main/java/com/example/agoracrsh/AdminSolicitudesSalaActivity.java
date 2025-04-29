package com.example.agoracrsh;

import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.agoracrsh.adapter.SolicitudSalaAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminSolicitudesSalaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SolicitudSalaAdapter adapter;
    private List<Map<String, Object>> listaSolicitudes;
    private List<String> listaIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solicitudes_sala);
        setTitle("Solicitudes de Sala");

        recyclerView = findViewById(R.id.recyclerSolicitudes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaSolicitudes = new ArrayList<>();
        listaIds = new ArrayList<>();

        cargarSolicitudesPendientes();
    }

    private void cargarSolicitudesPendientes() {
        FirebaseFirestore.getInstance()
                .collection("reservas")
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnSuccessListener(query -> {
                    listaSolicitudes.clear();
                    listaIds.clear();

                    for (var doc : query) {
                        listaSolicitudes.add(doc.getData());
                        listaIds.add(doc.getId());
                    }

                    adapter = new SolicitudSalaAdapter(listaSolicitudes, listaIds, this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
    }
}
