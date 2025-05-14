package com.example.agoracrsh;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class EquipoReservaActivity extends AppCompatActivity {

    // Elementos de la interfaz: selectores (spinners), campo de texto y botón
    private Spinner spinnerEquipo, spinnerDia, spinnerBloque;
    private EditText etCurso;
    private Button btnEnviar;

    // Lista de días de la semana para validar reservas
    private final List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_equipo);
        setTitle("Solicitud de Equipos");

        // Asociar los componentes de la interfaz con sus IDs
        spinnerEquipo = findViewById(R.id.spinnerTipoEquipo);
        spinnerDia = findViewById(R.id.spinnerDia);
        spinnerBloque = findViewById(R.id.spinnerBloque);
        etCurso = findViewById(R.id.etCurso);
        btnEnviar = findViewById(R.id.btnEnviarSolicitud);

        // Configurar adaptadores para los spinners (menús desplegables)
        ArrayAdapter<CharSequence> equipoAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.tipo_equipo_array));
        spinnerEquipo.setAdapter(equipoAdapter);
        spinnerEquipo.setSelection(0, false); // No seleccionar ninguno por defecto

        ArrayAdapter<CharSequence> diaAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.dias_semana_array));
        spinnerDia.setAdapter(diaAdapter);
        spinnerDia.setSelection(0, false);

        ArrayAdapter<CharSequence> bloqueAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.bloques_horarios_array));
        spinnerBloque.setAdapter(bloqueAdapter);
        spinnerBloque.setSelection(0, false);

        // Acción del botón de enviar
        btnEnviar.setOnClickListener(v -> enviarSolicitud());
    }

    // Método que valida y envía la solicitud a Firebase
    private void enviarSolicitud() {
        // Obtener los valores seleccionados por el usuario
        String equipo = spinnerEquipo.getSelectedItem().toString();
        String dia = spinnerDia.getSelectedItem().toString();
        String bloque = spinnerBloque.getSelectedItem().toString();
        String curso = etCurso.getText().toString().trim();

        // Validar que el curso no esté vacío
        if (curso.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validaciones para cada spinner (debe elegir una opción válida)
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

        // Validar que la reserva sea dentro de la semana actual
        if (!esSemanaActual(dia)) {
            Toast.makeText(this, "Solo puedes reservar para esta semana", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear mapa con los datos de la solicitud
        Map<String, Object> solicitud = new HashMap<>();
        solicitud.put("tipoEquipo", equipo);
        solicitud.put("dia", dia);
        solicitud.put("bloque", bloque);
        solicitud.put("curso", curso);
        solicitud.put("estado", "pendiente"); // Por defecto se marca como pendiente

        // Enviar los datos a la colección "reserva_equipo" en Firestore
        FirebaseFirestore.getInstance().collection("reserva_equipo")
                .add(solicitud)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la pantalla después de enviar
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al enviar la solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // Método que valida si el día seleccionado pertenece a la semana actual
    private boolean esSemanaActual(String diaSeleccionado) {
        Calendar hoy = Calendar.getInstance();
        int diaHoy = hoy.get(Calendar.DAY_OF_WEEK); // 1 = domingo, 2 = lunes, ..., 7 = sábado
        int diaSeleccionadoIndex = diasSemana.indexOf(diaSeleccionado); // Índice en la lista (0 = lunes)

        // Ajuste: Se suma 2 porque en Calendar, lunes es 2. También se asegura que no sea sábado o domingo
        return diaSeleccionadoIndex != -1 && (diaSeleccionadoIndex + 2) >= diaHoy && diaSeleccionadoIndex <= 4;
    }
}
