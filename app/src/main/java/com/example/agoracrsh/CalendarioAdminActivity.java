package com.example.agoracrsh;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CalendarioAdminActivity extends AppCompatActivity {

    private TableLayout tablaSala1, tablaSala2, tablaEquipos;
    private FirebaseFirestore firestore;

    private final List<String> bloques = Arrays.asList(
            "08:00 - 09:30", "09:45 - 11:15", "11:25 - 12:55",
            "13:55 - 15:25", "15:35 - 17:05"
    );

    // Días completos para las claves
    private final List<String> dias = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");
    // Días abreviados para el encabezado
    private final List<String> diasAbreviados = Arrays.asList("L", "M", "Mi", "J", "V");

    private final Map<String, Map<String, String>> reservasSala1 = new HashMap<>();
    private final Map<String, Map<String, String>> reservasSala2 = new HashMap<>();

    private final Set<String> reservasTICFijas = new HashSet<>(Arrays.asList(
            "08:00 - 09:30_Lunes", "09:45 - 11:15_Lunes", "13:55 - 15:25_Lunes", "15:35 - 17:05_Lunes",
            "09:45 - 11:15_Martes",
            "08:00 - 09:30_Miércoles", "11:25 - 12:55_Miércoles", "13:55 - 15:25_Miércoles",
            "11:25 - 12:55_Jueves", "13:55 - 15:25_Jueves", "15:35 - 17:05_Jueves",
            "08:00 - 09:30_Viernes", "09:45 - 11:15_Viernes", "11:25 - 12:55_Viernes"
    ));

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
                        Boolean expirada = doc.getBoolean("expirada");
                        if (expirada != null && expirada) continue;

                        String dia = doc.getString("dia");
                        String hora = doc.getString("hora");
                        String sala = doc.getString("sala");
                        String curso = doc.getString("curso");
                        String estado = doc.getString("estado");
                        String funcionario = doc.getString("funcionario");

                        if (dia != null && hora != null && sala != null) {
                            String key = hora + "_" + dia;
                            String datos = estado != null && estado.equals("pendiente")
                                    ? "Pendiente"
                                    : "Curso: " + curso + "\nProf: " + funcionario;

                            if (sala.equalsIgnoreCase("Sala 1")) {
                                reservasSala1.computeIfAbsent(key, k -> new HashMap<>()).put("info", datos);
                            } else if (sala.equalsIgnoreCase("Sala 2")) {
                                reservasSala2.computeIfAbsent(key, k -> new HashMap<>()).put("info", datos);
                            }
                        }
                    }

                    mostrarTabla(tablaSala1, reservasSala1, true);
                    mostrarTabla(tablaSala2, reservasSala2, false);
                });
    }

    private void mostrarTabla(TableLayout tabla, Map<String, Map<String, String>> reservas, boolean esSala1) {
        tabla.removeAllViews();

        // Encabezado
        TableRow encabezado = new TableRow(this);
        agregarCelda(encabezado, "Hora", true);
        for (String diaAbreviado : diasAbreviados) {
            agregarCelda(encabezado, diaAbreviado, true);
        }
        tabla.addView(encabezado);

        // Filas
        for (String bloque : bloques) {
            TableRow fila = new TableRow(this);
            agregarCelda(fila, bloque, true);

            for (int i = 0; i < dias.size(); i++) {
                String dia = dias.get(i);
                String key = bloque + "_" + dia;
                TextView celda = new TextView(this);
                celda.setGravity(Gravity.CENTER);
                celda.setPadding(12, 10, 12, 10);
                celda.setTextSize(11f);

                if (esSala1 && reservasTICFijas.contains(key)) {
                    celda.setText("TIC");
                    celda.setBackgroundColor(Color.parseColor("#90CAF9"));
                    celda.setTextColor(Color.BLACK);
                } else if (reservas.containsKey(key)) {
                    String info = reservas.get(key).get("info");
                    if ("Pendiente".equals(info)) {
                        celda.setText("Pendiente");
                        celda.setBackgroundColor(Color.parseColor("#FFCC80"));
                        celda.setTextColor(Color.BLACK);
                    } else {
                        celda.setText(info);
                        celda.setBackgroundColor(Color.parseColor("#EF9A9A"));
                        celda.setTextColor(Color.WHITE);
                    }
                } else {
                    celda.setText("Disponible");
                    celda.setBackgroundColor(Color.parseColor("#A5D6A7"));
                    celda.setTextColor(Color.BLACK);
                }

                fila.addView(celda);
            }

            tabla.addView(fila);
        }
    }

    private void agregarCelda(TableRow fila, String texto, boolean esEncabezado) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setGravity(Gravity.CENTER);
        celda.setPadding(12, 10, 12, 10);
        celda.setTextSize(esEncabezado ? 13f : 11f);
        celda.setTextColor(Color.BLACK);
        celda.setBackgroundColor(esEncabezado ? Color.parseColor("#C8E6C9") : Color.WHITE);
        fila.addView(celda);
    }

    private void cargarReservasEquipos() {
        firestore.collection("reserva_equipo")
                .get()
                .addOnSuccessListener(query -> {
                    tablaEquipos.removeAllViews();

                    for (var doc : query) {
                        Boolean expirada = doc.getBoolean("expirada");
                        if (expirada != null && expirada) continue;

                        String equipo = doc.getString("tipoEquipo");
                        String dia = doc.getString("dia");
                        String funcionario = doc.getString("funcionario");
                        String estado = doc.getString("estado");

                        TableRow fila = new TableRow(this);
                        fila.setPadding(4, 4, 4, 4);

                        fila.addView(crearCeldaEquipo(equipo));
                        fila.addView(crearCeldaEquipo(dia));
                        fila.addView(crearCeldaEquipo(funcionario));
                        fila.addView(crearCeldaEquipo(estado));

                        tablaEquipos.addView(fila);
                    }
                });
    }

    private TextView crearCeldaEquipo(String texto) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setPadding(12, 10, 12, 10);
        celda.setTextSize(12f);
        celda.setGravity(Gravity.CENTER);
        celda.setBackgroundColor(Color.LTGRAY);
        return celda;
    }
}
