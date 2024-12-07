package com.pupuseriajenny.ordenes.AuthManager;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.pupuseriajenny.ordenes.ui.activity.LoginActivity;
import com.pupuseriajenny.ordenes.data.service.AuthService;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.DTOs.LoginResponse;
import com.pupuseriajenny.ordenes.utils.TokenUtil;
import com.pupuseriajenny.ordenes.utils.JWTUtil;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {

    private final Context context;
    private final TokenUtil tokenUtil;

    public AuthManager(Context context) {
        this.context = context;
        this.tokenUtil = new TokenUtil(context);
    }

    // Verificar si el token está expirado
    public void verificarExpiracionDelToken() {
        String token = tokenUtil.getToken();
        if (token != null) {
            Date fechaExpiracion = JWTUtil.obtenerFechaExpiracionDesdeJWT(token);
            Date ahora = new Date();

            if (fechaExpiracion != null && fechaExpiracion.before(ahora)) {
                // Si el token ha expirado, eliminarlo
                tokenUtil.removeToken();
                Toast.makeText(context, "Tu sesión ha expirado. Vuelve a iniciar sesión.", Toast.LENGTH_SHORT).show();
                redirigirALogin();
            } else {
                // Si está cerca de expirar, preguntar si lo quiere renovar
                long diferenciaEnMillis = fechaExpiracion.getTime() - ahora.getTime();
                long minutosRestantes = diferenciaEnMillis / (1000 * 10); // Convierte a minutos

                if (minutosRestantes <= 10) {
                    mostrarDialogoRenovarToken();
                    Toast.makeText(context, "Tu sesión ha sido renovada.", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    // Mostrar el diálogo para renovar el token
    private void mostrarDialogoRenovarToken() {
        new AlertDialog.Builder(context)
                .setTitle("Sesión a punto de expirar")
                .setMessage("¿Deseas renovar tu sesión para continuar?")
                .setCancelable(false) // No se puede cerrar sin respuesta
                .setPositiveButton("Renovar", (dialog, which) -> renovarToken())
                .setNegativeButton("Cerrar sesión", (dialog, which) -> cerrarSesion())
                .show();
    }

    // Renovar el token
    private void renovarToken() {
        String token = tokenUtil.getToken();
        AuthService authService = RetrofitClient.getClient(context).create(AuthService.class);

        authService.renovarToken(token).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nuevoToken = response.body().getToken();
                    tokenUtil.saveToken(nuevoToken); // Guardar el nuevo token
                    Toast.makeText(context, "Tu sesión ha sido renovada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No se pudo renovar la sesión", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Error al renovar sesión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cerrar la sesión y redirigir al login
    public void cerrarSesion() {
        tokenUtil.removeToken(); // Eliminar el token
        redirigirALogin();
    }

    // Redirigir al login
    private void redirigirALogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
