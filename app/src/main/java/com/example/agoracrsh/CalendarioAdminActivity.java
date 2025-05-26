package com.example.agoracrsh;

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
                            String datos = "Curso: " + curso + "\nProf: " + funcionario + "\nEstado: " + estado;

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
        tabla.removeAllViews();

        for (String bloque : bloques) {
            TableRow fila = new TableRow(this);

            TextView txtHora = new TextView(this);
            txtHora.setText(bloque);
            txtHora.setGravity(Gravity.CENTER);
            txtHora.setTextSize(13f);
            txtHora.setPadding(16, 16, 16, 16);
            txtHora.setBackgroundResource(R.drawable.celda_hora);
            fila.addView(txtHora);

            for (String dia : dias) {
                String key = bloque + "_" + dia;
                TextView celda = new TextView(this);
                celda.setTextSize(12f);
                celda.setPadding(14, 12, 14, 12);
                celda.setGravity(Gravity.CENTER_VERTICAL);

                if (reservas.containsKey(key)) {
                    celda.setText(reservas.get(key).get("info"));
                    celda.setBackgroundResource(R.drawable.celda_ocupada);
                } else {
                    celda.setText("Disponible");
                    celda.setBackgroundResource(R.drawable.celda_disponible);
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
                    tablaEquipos.removeAllViews();

                    for (var doc : query) {
                        String equipo = doc.getString("tipoEquipo");
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
        celda.setPadding(10, 8, 10, 8);
        celda.setTextSize(12f);
        celda.setBackgroundResource(R.drawable.celda_equipo);
        return celda;
    }
}
