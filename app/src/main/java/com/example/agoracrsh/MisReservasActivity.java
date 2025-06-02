package com.example.agoracrsh;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MisReservasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout layoutReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);
        setTitle("Mis Reservas");

        db = FirebaseFirestore.getInstance();
        layoutReservas = findViewById(R.id.layoutReservas);

        cargarReservas("reserva_salas", "sala");
        cargarReservas("reserva_equipo", "equipo");
    }

    private void cargarReservas(String coleccion, String tipo) {
        String correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection(coleccion)
                .whereEqualTo("funcionario", correo)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        Boolean expirada = doc.getBoolean("expirada");
                        if (expirada != null && expirada) continue; // Ignorar reservas expiradas

                        String id = doc.getId();
                        String dia = doc.getString("dia");
                        String hora = doc.getString("hora");
                        String recurso = tipo.equals("sala") ? doc.getString("sala") : doc.getString("equipo");
                        String curso = doc.getString("curso");
                        String estado = doc.getString("estado");

                        View card = getLayoutInflater().inflate(R.layout.reservas_item, null);
                        TextView info = card.findViewById(R.id.infoReservaTextView);
                        Button btnCancelar = card.findViewById(R.id.btnCancelarReserva);
                        Button btnEditar = card.findViewById(R.id.btnEditarReserva);

                        info.setText("Día: " + dia + "\nHora: " + hora +
                                "\n" + tipo.toUpperCase() + ": " + recurso +
                                "\nCurso: " + curso + "\nEstado: " + estado);

                        btnCancelar.setOnClickListener(v -> confirmarCancelacion(coleccion, id, curso, recurso, dia, hora, tipo, correo));
                        btnEditar.setOnClickListener(v -> editarReserva(coleccion, id, tipo, curso, recurso, correo, dia, hora));

                        layoutReservas.addView(card);
                    }

                    if (query.isEmpty() && layoutReservas.getChildCount() == 0) {
                        Toast.makeText(this, "No tienes reservas registradas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar reservas", Toast.LENGTH_SHORT).show());
    }

    private void confirmarCancelacion(String coleccion, String reservaId, String curso, String recurso, String dia, String hora, String tipo, String correo) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás segurx que quieres cancelar la reserva?")
                .setPositiveButton("Sí", (dialog, which) -> cancelarReserva(coleccion, reservaId, curso, recurso, dia, hora, tipo, correo))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarReserva(String coleccion, String reservaId, String curso, String recurso, String dia, String hora, String tipo, String correo) {
        db.collection(coleccion).document(reservaId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Reserva cancelada", Toast.LENGTH_SHORT).show();
                    layoutReservas.removeAllViews();
                    cargarReservas("reserva_salas", "sala");
                    cargarReservas("reserva_equipo", "equipo");

                    Map<String, Object> noti = new HashMap<>();
                    noti.put("titulo", "Reserva cancelada");
                    noti.put("mensaje", curso + " canceló la reserva de " + tipo + ": " + recurso + " el " + dia + " a las " + hora);
                    noti.put("timestamp", System.currentTimeMillis());
                    noti.put("tipo", "cancelacion_reserva");
                    noti.put("usuario", correo);

                    db.collection("notificaciones_admin").add(noti);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cancelar reserva", Toast.LENGTH_SHORT).show());
    }

    private void editarReserva(String coleccion, String reservaId, String tipo, String cursoActual, String recursoActual, String correo, String dia, String hora) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText inputCurso = new EditText(this);
        inputCurso.setText(cursoActual);
        inputCurso.setHint("Nuevo curso");
        layout.addView(inputCurso);

        EditText inputRecurso = new EditText(this);
        inputRecurso.setText(recursoActual);
        inputRecurso.setHint(tipo.equals("sala") ? "Nueva sala" : "Nuevo equipo");
        layout.addView(inputRecurso);

        new AlertDialog.Builder(this)
                .setTitle("Editar Reserva")
                .setMessage("Modifica los datos de esta reserva:")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoCurso = inputCurso.getText().toString().trim();
                    String nuevoRecurso = inputRecurso.getText().toString().trim();

                    if (!nuevoCurso.isEmpty() && !nuevoRecurso.isEmpty()) {
                        db.collection(coleccion).document(reservaId)
                                .update("curso", nuevoCurso,
                                        tipo.equals("sala") ? "sala" : "equipo", nuevoRecurso)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Reserva actualizada", Toast.LENGTH_SHORT).show();
                                    layoutReservas.removeAllViews();
                                    cargarReservas("reserva_salas", "sala");
                                    cargarReservas("reserva_equipo", "equipo");

                                    Map<String, Object> noti = new HashMap<>();
                                    noti.put("titulo", "Reserva editada");
                                    noti.put("mensaje", nuevoCurso + " editó una reserva de " + tipo + ": " + nuevoRecurso + " el " + dia + " a las " + hora);
                                    noti.put("timestamp", System.currentTimeMillis());
                                    noti.put("tipo", "edicion_reserva");
                                    noti.put("usuario", correo);

                                    db.collection("notificaciones_admin").add(noti);
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
