package com.example.agoracrsh;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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
        String correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (curso.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el curso", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerEquipo.getSelectedItemPosition() == 0 ||
                spinnerDia.getSelectedItemPosition() == 0 ||
                spinnerBloque.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debes seleccionar todas las opciones", Toast.LENGTH_SHORT).show();
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
        solicitud.put("funcionario", correo); // NUEVO

        FirebaseFirestore.getInstance().collection("reserva_equipo")
                .add(solicitud)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
                    enviarNotificacionAdmin(correo, curso, equipo, dia, bloque);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al enviar la solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void enviarNotificacionAdmin(String correo, String curso, String equipo, String dia, String bloque) {
        Map<String, Object> noti = new HashMap<>();
        noti.put("titulo", "Nueva solicitud de equipo");
        noti.put("mensaje", curso + " solicitó el equipo " + equipo + " el " + dia + " a las " + bloque);
        noti.put("timestamp", System.currentTimeMillis());
        noti.put("tipo", "reserva_equipo");
        noti.put("usuario", correo);

        FirebaseFirestore.getInstance().collection("notificaciones_admin").add(noti);
    }

    private boolean esSemanaActual(String diaSeleccionado) {
        Calendar hoy = Calendar.getInstance();
        int diaHoy = hoy.get(Calendar.DAY_OF_WEEK);
        int diaSeleccionadoIndex = diasSemana.indexOf(diaSeleccionado);
        return diaSeleccionadoIndex != -1 && (diaSeleccionadoIndex + 2) >= diaHoy && diaSeleccionadoIndex <= 4;
    }
}
