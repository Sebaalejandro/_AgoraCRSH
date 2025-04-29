package com.example.agoracrsh;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class SalaReservaActivity extends AppCompatActivity {

    private TableLayout tableLayoutHorario;
    private FirebaseFirestore firestore;

    private final List<String> bloques = Arrays.asList(
            "08:00 - 09:30", "09:45 - 11:15", "11:25 - 12:55",
            "13:55 - 15:25", "15:35 - 17:05"
    );

    private final List<String> diasAbreviados = Arrays.asList("L", "M", "X", "J", "V");

    private final Map<String, String> diaCompletoPorAbreviado = new HashMap<String, String>() {{
        put("L", "Lunes");
        put("M", "Martes");
        put("X", "Miércoles");
        put("J", "Jueves");
        put("V", "Viernes");
    }};

    private final Map<String, Map<String, String>> reservasExistentes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_reserva);
        setTitle("Reservar Sala");

        tableLayoutHorario = findViewById(R.id.tableLayoutHorario);
        firestore = FirebaseFirestore.getInstance();

        cargarReservasDesdeFirestore();
    }

    private void cargarReservasDesdeFirestore() {
        firestore.collection("reserva_salas")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        String dia = doc.getString("dia");
                        String hora = doc.getString("hora");
                        String sala = doc.getString("sala");
                        String estado = doc.getString("estado");

                        if (dia != null && hora != null && sala != null && estado != null) {
                            String key = hora + "_" + dia;
                            reservasExistentes
                                    .computeIfAbsent(key, k -> new HashMap<>())
                                    .put(sala, estado);
                        }
                    }
                    cargarHorarioConBloqueo();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar reservas", Toast.LENGTH_SHORT).show());
    }

    private void cargarHorarioConBloqueo() {
        for (String bloque : bloques) {
            TableRow fila = new TableRow(this);

            TextView txtHora = new TextView(this);
            txtHora.setText(bloque);
            txtHora.setTextColor(Color.BLACK);
            txtHora.setGravity(Gravity.CENTER);
            txtHora.setPadding(16, 8, 16, 8);
            fila.addView(txtHora);

            for (String diaAbrev : diasAbreviados) {
                String diaCompleto = diaCompletoPorAbreviado.get(diaAbrev);

                Button btn = new Button(this);
                btn.setTextSize(10f);
                btn.setAllCaps(false);
                btn.setPadding(8, 4, 8, 4);

                String key = bloque + "_" + diaCompleto;
                String sala = "Sala 1";

                String estado = reservasExistentes.containsKey(key) ?
                        reservasExistentes.get(key).getOrDefault(sala, "disponible") :
                        "disponible";

                configurarBoton(btn, estado, diaCompleto, bloque, sala);
                fila.addView(btn);
            }

            tableLayoutHorario.addView(fila, new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    private void configurarBoton(Button btn, String estado, String dia, String hora, String sala) {
        switch (estado) {
            case "ocupado":
                btn.setText("Ocupado");
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.parseColor("#EF9A9A")); // rojo
                btn.setTextColor(Color.WHITE);
                break;
            case "pendiente":
                btn.setText("Pendiente");
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.parseColor("#FFCC80")); // naranjo
                btn.setTextColor(Color.BLACK);
                break;
            default:
                btn.setText("Reservar");
                btn.setEnabled(true);
                btn.setBackgroundColor(Color.parseColor("#A5D6A7")); // verde
                btn.setTextColor(Color.BLACK);
                btn.setOnClickListener(v -> mostrarFormularioReserva(dia, hora, sala));
        }
    }

    // Mostrar campo para ingresar curso
    private void mostrarFormularioReserva(String dia, String hora, String sala) {
        final EditText inputCurso = new EditText(this);
        inputCurso.setHint("Ej: 4° Medio B");

        new AlertDialog.Builder(this)
                .setTitle("Reservar Sala")
                .setMessage("Ingresa el curso con el que ocuparás la sala:")
                .setView(inputCurso)
                .setPositiveButton("Siguiente", (dialog, which) -> {
                    String curso = inputCurso.getText().toString().trim();
                    if (curso.isEmpty()) {
                        Toast.makeText(this, "Debes ingresar el curso", Toast.LENGTH_SHORT).show();
                    } else {
                        mostrarConfirmacionFinal(dia, hora, sala, curso);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Confirmar antes de enviar reserva
    private void mostrarConfirmacionFinal(String dia, String hora, String sala, String curso) {
        String mensaje = "¿Estás seguro que deseas reservar " + sala +
                " el día " + dia + " a las " + hora + " para el curso \"" + curso + "\"?";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Reserva")
                .setMessage(mensaje)
                .setPositiveButton("Confirmar", (dialog, which) -> enviarSolicitud(dia, hora, sala, curso))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Enviar a Firestore
    private void enviarSolicitud(String dia, String hora, String sala, String curso) {
        Map<String, Object> reserva = new HashMap<>();
        reserva.put("dia", dia);
        reserva.put("hora", hora);
        reserva.put("sala", sala);
        reserva.put("curso", curso);
        reserva.put("estado", "pendiente");
        reserva.put("tipo", "sala"); // <-- ¡ESTO ES CLAVE!
        reserva.put("funcionario", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        firestore.collection("reserva_salas")
                .add(reserva)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Solicitud enviada para aprobación", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al reservar", Toast.LENGTH_SHORT).show());
    }
}
