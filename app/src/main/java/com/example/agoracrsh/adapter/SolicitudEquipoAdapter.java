package com.example.agoracrsh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agoracrsh.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class SolicitudEquipoAdapter extends RecyclerView.Adapter<SolicitudEquipoAdapter.ViewHolder> {

    // Lista de solicitudes con sus datos
    private final List<Map<String, Object>> listaSolicitudes;

    // Lista de IDs de documentos en Firestore
    private final List<String> listaIds;

    // Contexto necesario para inflar vistas y mostrar Toasts
    private final Context context;

    // Constructor del adaptador
    public SolicitudEquipoAdapter(List<Map<String, Object>> listaSolicitudes, List<String> listaIds, Context context) {
        this.listaSolicitudes = listaSolicitudes;
        this.listaIds = listaIds;
        this.context = context;
    }

    // Crea e infla la vista del item
    @NonNull
    @Override
    public SolicitudEquipoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud_equipo, parent, false);
        return new ViewHolder(view);
    }

    // Asocia los datos con la vista
    @Override
    public void onBindViewHolder(@NonNull SolicitudEquipoAdapter.ViewHolder holder, int position) {
        Map<String, Object> solicitud = listaSolicitudes.get(position);

        // Obtener datos de la solicitud
        String tipoEquipo = String.valueOf(solicitud.get("tipoEquipo"));
        String dia = String.valueOf(solicitud.get("dia"));
        String bloque = String.valueOf(solicitud.get("bloque"));
        String curso = String.valueOf(solicitud.get("curso"));

        // Mostrar datos en el TextView
        holder.txtDetalle.setText("Equipo: " + tipoEquipo +
                "\nDía: " + dia +
                "\nBloque: " + bloque +
                "\nCurso: " + curso);

        // Acción del botón Aprobar
        holder.btnAprobar.setOnClickListener(v -> {
            String id = listaIds.get(position);
            FirebaseFirestore.getInstance().collection("reserva_equipo")
                    .document(id)
                    .update("estado", "aprobado")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Solicitud aprobada", Toast.LENGTH_SHORT).show());
        });

        // Acción del botón Rechazar
        holder.btnRechazar.setOnClickListener(v -> {
            String id = listaIds.get(position);
            FirebaseFirestore.getInstance().collection("reserva_equipo")
                    .document(id)
                    .update("estado", "rechazado")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show());
        });
    }

    // Devuelve el total de ítems en la lista
    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    // Clase interna ViewHolder para mantener las vistas del ítem
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDetalle;
        Button btnAprobar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDetalle = itemView.findViewById(R.id.solicitudEquipoInfoTextView);
            btnAprobar = itemView.findViewById(R.id.btnAceptarEquipo);
            btnRechazar = itemView.findViewById(R.id.btnRechazarEquipo);
        }
    }
}
