package com.pupuseriajenny.ordenes.ui.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.pupuseriajenny.ordenes.ApiService;
import com.pupuseriajenny.ordenes.DTOs.CategoriaResponse;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.ui.activity.BebidaActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriaManager {

    private Context context;
    private GridLayout gridLayout;

    public CategoriaManager(Context context, GridLayout gridLayout) {
        this.context = context;
        this.gridLayout = gridLayout;
    }

    public void obtenerCategorias() {
        SharedPreferences prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token != null) {
            ApiService apiService = RetrofitClient.getClient(context)
                    .create(ApiService.class);

            Call<CategoriaResponse> call = apiService.obtenerCategorias();

            call.enqueue(new Callback<CategoriaResponse>() {
                @Override
                public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CategoriaResponse categoriaResponse = response.body();
                        List<String> categorias = categoriaResponse.getCategoria();
                        if (categorias != null && !categorias.isEmpty()) {
                            agregarBotonesCategorias(categorias);
                        }
                    } else {
                        Toast.makeText(context, "No se encontraron categorías", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CategoriaResponse> call, Throwable t) {
                    Toast.makeText(context, "Error al cargar las categorías", Toast.LENGTH_SHORT).show();
                    Log.e("CategoriaManager", "Error en la llamada a la API", t);
                }
            });
        } else {
            Toast.makeText(context, "Token no encontrado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarBotonesCategorias(List<String> categorias) {
        gridLayout.removeAllViews();

        for (String categoria : categorias) {
            // Inflar el layout para cada categoría
            Button button = new Button(context);
            button.setText(categoria);
            button.setOnClickListener(v -> {
                Toast.makeText(context, "Categoría seleccionada: " + categoria, Toast.LENGTH_SHORT).show();
                // Crear un Intent para navegar a la siguiente actividad
                Intent intent = new Intent(context, BebidaActivity.class);
                intent.putExtra("categoria", categoria);  // Pasar el nombre de la categoría

                // Iniciar la actividad
                context.startActivity(intent);
            });

            gridLayout.addView(button);
        }
    }
}
