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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

                        btnCancelar.setOnClickListener(v -> confirmarCancelacion(coleccion, id));
                        btnEditar.setOnClickListener(v -> editarReserva(coleccion, id, tipo, curso, recurso));

                        layoutReservas.addView(card);
                    }

                    if (query.isEmpty() && layoutReservas.getChildCount() == 0) {
                        Toast.makeText(this, "No tienes reservas registradas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar reservas", Toast.LENGTH_SHORT).show());
    }

    private void confirmarCancelacion(String coleccion, String reservaId) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás segurx que quieres cancelar la reserva?")
                .setPositiveButton("Sí", (dialog, which) -> cancelarReserva(coleccion, reservaId))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelarReserva(String coleccion, String reservaId) {
        db.collection(coleccion).document(reservaId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Reserva cancelada", Toast.LENGTH_SHORT).show();
                    layoutReservas.removeAllViews();
                    cargarReservas("reserva_salas", "sala");
                    cargarReservas("reserva_equipo", "equipo");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cancelar reserva", Toast.LENGTH_SHORT).show());
    }

    private void editarReserva(String coleccion, String reservaId, String tipo, String cursoActual, String recursoActual) {
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
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
