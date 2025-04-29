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

    private final List<Map<String, Object>> listaSolicitudes;
    private final List<String> listaIds;
    private final Context context;

    public SolicitudEquipoAdapter(List<Map<String, Object>> listaSolicitudes, List<String> listaIds, Context context) {
        this.listaSolicitudes = listaSolicitudes;
        this.listaIds = listaIds;
        this.context = context;
    }

    @NonNull
    @Override
    public SolicitudEquipoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud_equipo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudEquipoAdapter.ViewHolder holder, int position) {
        Map<String, Object> solicitud = listaSolicitudes.get(position);

        String tipoEquipo = String.valueOf(solicitud.get("tipoEquipo"));
        String dia = String.valueOf(solicitud.get("dia"));
        String bloque = String.valueOf(solicitud.get("bloque"));
        String curso = String.valueOf(solicitud.get("curso"));

        holder.txtDetalle.setText("Equipo: " + tipoEquipo + "\nDía: " + dia + "\nBloque: " + bloque + "\nCurso: " + curso);

        // Botón de aprobar
        holder.btnAprobar.setOnClickListener(v -> {
            String id = listaIds.get(position);
            FirebaseFirestore.getInstance().collection("reserva_equipo") // ✅ CORREGIDO
                    .document(id)
                    .update("estado", "aprobado")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Solicitud aprobada", Toast.LENGTH_SHORT).show());
        });

        // Botón de rechazar
        holder.btnRechazar.setOnClickListener(v -> {
            String id = listaIds.get(position);
            FirebaseFirestore.getInstance().collection("reserva_equipo")
                    .document(id)
                    .update("estado", "rechazado")
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

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
