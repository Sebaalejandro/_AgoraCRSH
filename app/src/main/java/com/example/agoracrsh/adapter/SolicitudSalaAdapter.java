package com.example.agoracrsh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agoracrsh.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class SolicitudSalaAdapter extends RecyclerView.Adapter<SolicitudSalaAdapter.SolicitudViewHolder> {

    private final List<Map<String, Object>> listaSolicitudes;
    private final List<String> idsDocumentos;
    private final Context context;

    public SolicitudSalaAdapter(List<Map<String, Object>> listaSolicitudes, List<String> idsDocumentos, Context context) {
        this.listaSolicitudes = listaSolicitudes;
        this.idsDocumentos = idsDocumentos;
        this.context = context;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud_sala, parent, false);
        return new SolicitudViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Map<String, Object> solicitud = listaSolicitudes.get(position);
        String docId = idsDocumentos.get(position);

        String info = "Sala: " + solicitud.get("sala") +
                "\nDía: " + solicitud.get("dia") +
                "\nHora: " + solicitud.get("hora") +
                "\nCurso: " + solicitud.get("curso") +
                "\nProfesor: " + solicitud.get("profesor");

        holder.infoTextView.setText(info);

        holder.btnAceptar.setOnClickListener(v -> actualizarEstado(docId, "ocupado"));
        holder.btnRechazar.setOnClickListener(v -> actualizarEstado(docId, "rechazado"));
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    private void actualizarEstado(String id, String nuevoEstado) {
        FirebaseFirestore.getInstance()
                .collection("reservas")
                .document(id)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }

    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        TextView infoTextView;
        Button btnAceptar, btnRechazar;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.solicitudInfoTextView);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
