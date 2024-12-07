package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pupuseriajenny.ordenes.ApiService;
import com.pupuseriajenny.ordenes.AuthManager.AuthManager;
import com.pupuseriajenny.ordenes.DTOs.CategoriaResponse;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.utils.JWTUtil;
import com.pupuseriajenny.ordenes.utils.TokenUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private GridLayout gridLayoutCategorias;
    @Override
    protected void onStart() {
        super.onStart();

        // Crear instancia de AuthManager
        AuthManager authManager = new AuthManager(this);

        // Verificar la expiración del token cuando la actividad se inicia
        authManager.verificarExpiracionDelToken();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar GridLayout
        gridLayoutCategorias = findViewById(R.id.gridLayoutCategorias);

        // Llamar al servicio de Retrofit para obtener las categorías
        obtenerCategorias();
    }

    private void obtenerCategorias() {
        // Obtener el token de SharedPreferences (si es necesario para autenticar la solicitud)
        TokenUtil tokenUtil = new TokenUtil(this);

        if (tokenUtil.isTokenValid()) {
            // Crear el servicio API
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);


            // Llamada para obtener las categorías
            Call<CategoriaResponse> call = apiService.obtenerCategorias();
            call.enqueue(new Callback<CategoriaResponse>() {
                @Override
                public void onResponse(Call<CategoriaResponse> call, Response<CategoriaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CategoriaResponse categoriaResponse = response.body();
                        List<String> categorias = categoriaResponse.getCategoria();

                        if (categorias != null && !categorias.isEmpty()) {
                            // Recorrer las categorías
                            agregarBotonesCategorias(categoriaResponse);
                            for (String categoria : categorias) {
                                Log.d("Categoria", categoria);  // Esto debería imprimir "Bebidas" y "Pupusas"
                            }
                        }
                    } else if (response.code() == 401) {
                        // Token vencido o inválido
                        Toast.makeText(HomeActivity.this, "Sesión expirada. Por favor, inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();


                        tokenUtil.removeToken();

                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, "No se encontraron categorías", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CategoriaResponse> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, "Error al cargar las categorías", Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Error en la llamada a la API", t);
                }
            });

        } else {
            Toast.makeText(this, "Token no encontrado. Por favor, inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void agregarBotonesCategorias(CategoriaResponse categoriaResponse) {
        // Limpiar el GridLayout antes de agregar los nuevos elementos
        gridLayoutCategorias.removeAllViews();

        // Obtener la lista de categorías desde el objeto CategoriaResponse
        List<String> categorias = categoriaResponse.getCategoria();

        if (categorias != null && !categorias.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(this);

            // Recorrer la lista de categorías
            for (String categoria : categorias) {
                // Inflar un nuevo layout para cada categoría
                View buttonLayout = inflater.inflate(R.layout.button_category, gridLayoutCategorias, false);
                Button button = buttonLayout.findViewById(R.id.btnCategory);
                button.setText(categoria);

                button.setOnClickListener(v -> {
                    // Manejo del clic en cada categoría
                    Toast.makeText(HomeActivity.this, "Categoría seleccionada: " + categoria, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, BebidaActivity.class);
                    intent.putExtra("categoria",categoria);
                    startActivity(intent);
                });

                // Añadir el layout inflado al GridLayout
                gridLayoutCategorias.addView(buttonLayout);
            }
        }
    }

}
