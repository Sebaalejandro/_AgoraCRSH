package com.example.agoracrsh;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=TU_CLAVE_DEL_SERVIDOR"; // ← pega aquí tu clave del servidor FCM
    private static final String CONTENT_TYPE = "application/json";

    public static void enviarNotificacion(Context context, String tokenDestino, String titulo, String mensaje) {
        try {
            JSONObject noti = new JSONObject();
            noti.put("title", titulo);
            noti.put("body", mensaje);

            JSONObject body = new JSONObject();
            body.put("to", tokenDestino);
            body.put("notification", noti);

            StringRequest request = new StringRequest(Request.Method.POST, FCM_API,
                    response -> Log.d("FCM_OK", "Notificación enviada"),
                    error -> Log.e("FCM_ERROR", "Error al enviar notificación: " + error.getMessage())) {

                @Override
                public byte[] getBody() {
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return CONTENT_TYPE;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", SERVER_KEY);
                    headers.put("Content-Type", CONTENT_TYPE);
                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {
            Log.e("FCM_EXCEPTION", "Excepción: " + e.getMessage());
        }
    }
}
