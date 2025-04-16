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
import com.example.agoracrsh.model.BloqueHorario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.ViewHolder> {

    private final List<BloqueHorario> bloques;
    private final String fecha;
    private final FirebaseFirestore firestore;
    private final FirebaseUser user;
    private final Map<String, String> sala1Estados;
    private final Map<String, String> sala2Estados;

    public HorarioAdapter(List<BloqueHorario> bloques, String fecha, FirebaseFirestore firestore, FirebaseUser user,
                          Map<String, String> sala1Estados, Map<String, String> sala2Estados) {
        this.bloques = bloques;
        this.fecha = fecha;
        this.firestore = firestore;
        this.user = user;
        this.sala1Estados = sala1Estados;
        this.sala2Estados = sala2Estados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bloque, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloqueHorario bloque = bloques.get(position);
        String hora = bloque.getHora();
        holder.txtHora.setText(hora);

        String estado1 = sala1Estados.getOrDefault(hora, "disponible");
        String estado2 = sala2Estados.getOrDefault(hora, "disponible");

        configurarBoton(holder.btnSala1, estado1, hora, "Sala 1");
        configurarBoton(holder.btnSala2, estado2, hora, "Sala 2");
    }

    private void configurarBoton(Button boton, String estado, String hora, String sala) {
        switch (estado) {
            case "ocupado":
                boton.setText("Ocupado");
                boton.setEnabled(false);
                break;
            case "pendiente":
                boton.setText("Pendiente");
                boton.setEnabled(false);
                break;
            default:
                boton.setText(sala);
                boton.setEnabled(true);
                boton.setOnClickListener(v -> reservar(v.getContext(), sala, hora));
                break;
        }
    }

    private void reservar(Context context, String sala, String hora) {
        Map<String, Object> reserva = new HashMap<>();
        reserva.put("fecha", fecha);
        reserva.put("hora", hora);
        reserva.put("sala", sala);
        reserva.put("estado", "pendiente");
        reserva.put("profesor", user.getEmail());

        firestore.collection("reservas")
                .add(reserva)
                .addOnSuccessListener(doc -> Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return bloques.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtHora;
        Button btnSala1, btnSala2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtHora = itemView.findViewById(R.id.txtHora);
            btnSala1 = itemView.findViewById(R.id.btnSala1);
            btnSala2 = itemView.findViewById(R.id.btnSala2);
        }
    }
}
