package com.example.agoracrsh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class SalaReservaActivity extends AppCompatActivity {

    private TableLayout tablaHorario;
    private FirebaseFirestore firestore;

    private final List<String> bloques = Arrays.asList(
            "08:00 - 09:30", "09:45 - 11:15", "11:25 - 12:55",
            "13:55 - 15:25", "15:35 - 17:05"
    );

    private final List<String> dias = Arrays.asList("Lun", "Mar", "Mié", "Jue", "Vie");

    // Estructura para guardar reservas existentes por (día + hora)
    private final Map<String, Map<String, String>> reservasExistentes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_reserva);
        setTitle("Reservar Sala");

        tablaHorario = findViewById(R.id.tablaHorario);
        firestore = FirebaseFirestore.getInstance();


    }

    // Validación de semana actual
    private boolean esSemanaActual() {
        Calendar hoy = Calendar.getInstance();
        int semanaHoy = hoy.get(Calendar.WEEK_OF_YEAR);

        Calendar lunes = Calendar.getInstance();
        lunes.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Calendar domingo = Calendar.getInstance();
        domingo.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return hoy.getTime().after(lunes.getTime()) && hoy.getTime().before(domingo.getTime());
    }

    // Leer reservas existentes desde Firestore
    private void cargarReservasDesdeFirestore() {
        firestore.collection("reservas")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        String dia = doc.getString("dia");
                        String hora = doc.getString("hora");
                        String sala = doc.getString("sala");
                        String estado = doc.getString("estado");

                        if (dia != null && hora != null && sala != null && estado != null) {
                            String key = dia + "_" + hora;
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

    // Dibujar la tabla con estado de cada bloque
    private void cargarHorarioConBloqueo() {
        for (String bloque : bloques) {
            TableRow fila = new TableRow(this);

            TextView txtHora = new TextView(this);
            txtHora.setText(bloque);
            txtHora.setTextColor(Color.BLACK);
            txtHora.setGravity(Gravity.CENTER);
            txtHora.setPadding(16, 8, 16, 8);
            fila.addView(txtHora);

            for (String dia : dias) {
                for (int sala = 1; sala <= 2; sala++) {
                    Button btn = new Button(this);
                    btn.setTextSize(10f);
                    btn.setAllCaps(false);
                    btn.setPadding(8, 4, 8, 4);

                    String key = dia + "_" + bloque;
                    String salaNombre = "Sala " + sala;

                    String estado = reservasExistentes.containsKey(key) ?
                            reservasExistentes.get(key).getOrDefault(salaNombre, "disponible") :
                            "disponible";

                    configurarBoton(btn, estado, dia, bloque, salaNombre);
                    fila.addView(btn);
                }
            }

            tablaHorario.addView(fila, new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    // Configurar visualmente cada botón según estado
    private void configurarBoton(Button btn, String estado, String dia, String hora, String sala) {
        switch (estado) {
            case "ocupado":
                btn.setText("Ocupado");
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.RED);
                btn.setTextColor(Color.WHITE);
                break;
            case "pendiente":
                btn.setText("Pendiente");
                btn.setEnabled(false);
                btn.setBackgroundColor(Color.GRAY);
                btn.setTextColor(Color.WHITE);
                break;
            default:
                btn.setText("Reservar");
                btn.setEnabled(true);
                btn.setBackgroundColor(Color.parseColor("#188F97"));
                btn.setTextColor(Color.WHITE);
                btn.setOnClickListener(v -> enviarSolicitud(dia, hora, sala));
        }
    }

    // Guardar solicitud como pendiente en Firestore
    private void enviarSolicitud(String dia, String hora, String sala) {
        Map<String, Object> reserva = new HashMap<>();
        reserva.put("dia", dia);
        reserva.put("hora", hora);
        reserva.put("sala", sala);
        reserva.put("estado", "pendiente");
        reserva.put("profesor", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        firestore.collection("reservas")
                .add(reserva)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Solicitud enviada para aprobación", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al reservar", Toast.LENGTH_SHORT).show());
    }
}
