package com.example.agoracrsh;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarioAdminActivity extends AppCompatActivity {

    private TableLayout tablaSala1, tablaSala2, tablaEquipos;
    private FirebaseFirestore firestore;

    private final List<String> bloques = List.of(
            "08:00 - 09:30", "09:45 - 11:15", "11:25 - 12:55",
            "13:55 - 15:25", "15:35 - 17:05"
    );

    private final List<String> dias = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");

    private final Map<String, Map<String, String>> reservasSala1 = new HashMap<>();
    private final Map<String, Map<String, String>> reservasSala2 = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_admin);

        tablaSala1 = findViewById(R.id.tablaSala1);
        tablaSala2 = findViewById(R.id.tablaSala2);
        tablaEquipos = findViewById(R.id.tablaEquipos);
        firestore = FirebaseFirestore.getInstance();

        cargarReservasSalas();
        cargarReservasEquipos();
    }

    private void cargarReservasSalas() {
        firestore.collection("reserva_salas")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        String dia = doc.getString("dia");
                        String hora = doc.getString("hora");
                        String sala = doc.getString("sala");
                        String curso = doc.getString("curso");
                        String estado = doc.getString("estado");
                        String funcionario = doc.getString("funcionario");

                        if (dia != null && hora != null && sala != null) {
                            String key = hora + "_" + dia;
                            String datos = "Curso: " + curso + "\n" +
                                    "Prof: " + funcionario + "\n" +
                                    "Estado: " + estado;

                            if (sala.equals("Sala 1")) {
                                reservasSala1.computeIfAbsent(key, k -> new HashMap<>()).put("info", datos);
                            } else if (sala.equals("Sala 2")) {
                                reservasSala2.computeIfAbsent(key, k -> new HashMap<>()).put("info", datos);
                            }
                        }
                    }

                    mostrarTablaSalas(tablaSala1, reservasSala1);
                    mostrarTablaSalas(tablaSala2, reservasSala2);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar reservas de salas", Toast.LENGTH_SHORT).show());
    }

    private void mostrarTablaSalas(TableLayout tabla, Map<String, Map<String, String>> reservas) {
        for (String bloque : bloques) {
            TableRow fila = new TableRow(this);

            TextView txtHora = new TextView(this);
            txtHora.setText(bloque);
            txtHora.setTextColor(Color.BLACK);
            txtHora.setGravity(Gravity.CENTER);
            txtHora.setPadding(16, 8, 16, 8);
            fila.addView(txtHora);

            for (String dia : dias) {
                String key = bloque + "_" + dia;

                TextView celda = new TextView(this);
                celda.setPadding(8, 4, 8, 4);
                celda.setTextSize(11f);
                celda.setBackgroundColor(Color.LTGRAY);

                if (reservas.containsKey(key)) {
                    String texto = reservas.get(key).get("info");
                    celda.setText(texto);
                    celda.setBackgroundColor(Color.parseColor("#FFF176")); // amarillo
                } else {
                    celda.setText("Disponible");
                    celda.setBackgroundColor(Color.parseColor("#C8E6C9")); // verde claro
                }

                fila.addView(celda);
            }

            tabla.addView(fila);
        }
    }

    private void cargarReservasEquipos() {
        firestore.collection("reserva_equipo")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        String equipo = doc.getString("tipo_equipo");
                        String dia = doc.getString("dia");
                        String funcionario = doc.getString("funcionario");
                        String estado = doc.getString("estado");

                        TableRow fila = new TableRow(this);
                        fila.setPadding(4, 4, 4, 4);

                        fila.addView(crearCelda(equipo));
                        fila.addView(crearCelda(dia));
                        fila.addView(crearCelda(funcionario));
                        fila.addView(crearCelda(estado));

                        tablaEquipos.addView(fila);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar reservas de equipos", Toast.LENGTH_SHORT).show());
    }

    private TextView crearCelda(String texto) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setPadding(8, 4, 8, 4);
        celda.setTextSize(12f);
        celda.setBackgroundColor(Color.WHITE);
        return celda;
    }
}
