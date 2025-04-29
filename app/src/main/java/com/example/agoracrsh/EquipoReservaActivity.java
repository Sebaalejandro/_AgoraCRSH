package com.example.agoracrsh;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class EquipoReservaActivity extends AppCompatActivity {

    private Spinner spinnerEquipo, spinnerDia, spinnerBloque;
    private EditText etCurso;
    private Button btnEnviar;
    private final List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_equipo);
        setTitle("Solicitud de Equipos");

        spinnerEquipo = findViewById(R.id.spinnerTipoEquipo);
        spinnerDia = findViewById(R.id.spinnerDia);
        spinnerBloque = findViewById(R.id.spinnerBloque);
        etCurso = findViewById(R.id.etCurso);
        btnEnviar = findViewById(R.id.btnEnviarSolicitud);

        // Adaptadores con primera opción como hint
        ArrayAdapter<CharSequence> equipoAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.tipo_equipo_array));
        spinnerEquipo.setAdapter(equipoAdapter);
        spinnerEquipo.setSelection(0, false);

        ArrayAdapter<CharSequence> diaAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.dias_semana_array));
        spinnerDia.setAdapter(diaAdapter);
        spinnerDia.setSelection(0, false);

        ArrayAdapter<CharSequence> bloqueAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.bloques_horarios_array));
        spinnerBloque.setAdapter(bloqueAdapter);
        spinnerBloque.setSelection(0, false);

        btnEnviar.setOnClickListener(v -> enviarSolicitud());
    }

    private void enviarSolicitud() {
        String equipo = spinnerEquipo.getSelectedItem().toString();
        String dia = spinnerDia.getSelectedItem().toString();
        String bloque = spinnerBloque.getSelectedItem().toString();
        String curso = etCurso.getText().toString().trim();

        if (curso.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerEquipo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debes seleccionar un equipo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerDia.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debes seleccionar un día", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerBloque.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debes seleccionar un bloque horario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!esSemanaActual(dia)) {
            Toast.makeText(this, "Solo puedes reservar para esta semana", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("tipoEquipo", equipo);
        solicitud.put("dia", dia);
        solicitud.put("bloque", bloque);
        solicitud.put("curso", curso);
        solicitud.put("estado", "pendiente");

        // AHORA envía a la colección "reserva_equipo" para mantener todo ordenado
        FirebaseFirestore.getInstance().collection("reserva_equipo")
                .add(solicitud)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al enviar la solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private boolean esSemanaActual(String diaSeleccionado) {
        Calendar hoy = Calendar.getInstance();
        int diaHoy = hoy.get(Calendar.DAY_OF_WEEK); // 1 = domingo, 2 = lunes, etc.
        int diaSeleccionadoIndex = diasSemana.indexOf(diaSeleccionado); // 0 = lunes

        return diaSeleccionadoIndex != -1 && (diaSeleccionadoIndex + 2) >= diaHoy && diaSeleccionadoIndex <= 4;
    }
}
