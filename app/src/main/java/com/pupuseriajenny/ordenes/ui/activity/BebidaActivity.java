package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pupuseriajenny.ordenes.ApiService;
import com.pupuseriajenny.ordenes.AuthManager.AuthManager;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.data.model.Producto;
import com.pupuseriajenny.ordenes.ui.adapter.BebidaAdapter;
import com.pupuseriajenny.ordenes.ui.listener.BebidaActionsListener;
import com.pupuseriajenny.ordenes.utils.TokenUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BebidaActivity extends AppCompatActivity implements BebidaActionsListener {
    private List<Producto> bebidasSeleccionadas = new ArrayList<>();  // Lista para las bebidas seleccionadas

    // Declaración de vistas
    private TextView txtTitulo, txtCantidad;
    private RecyclerView recyclerView;
    private Button btnDetalles;
    private BebidaAdapter bebidaAdapter;
    private List<Producto> productoList = new ArrayList<>(); // Lista de productos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bebida); // Establecer el layout de la actividad
        // Crear instancia de AuthManager
        AuthManager authManager = new AuthManager(this);

        // Verificar la expiración del token cuando la actividad se inicia
        authManager.verificarExpiracionDelToken();
        // Inicializar vistas
        initializeViews();

        // Configurar datos y título
        setupBebidaList();

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bebidaAdapter = new BebidaAdapter(productoList, this);
        recyclerView.setAdapter(bebidaAdapter);

        // Recuperar el token desde SharedPreferences
        TokenUtil tokenUtil = new TokenUtil(this);

        // Recuperar la categoría desde el Intent
        Intent intent = getIntent();
        String categoria = intent.getStringExtra("categoria");

        if (categoria != null && tokenUtil.getToken() != null) {
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

            // Llamada para obtener los productos por categoría
            Call<List<Producto>> call = apiService.obtenerProductosPorCategoria(categoria);

            call.enqueue(new Callback<List<Producto>>() {
                @Override
                public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        productoList = response.body();
                        bebidaAdapter.actualizarLista(productoList); // Actualiza el adaptador con los nuevos datos

                        // Mostrar la cantidad de productos
                        txtCantidad.setText("Total de productos: " + productoList.size());
                    } else {
                        Toast.makeText(BebidaActivity.this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Producto>> call, Throwable t) {
                    Toast.makeText(BebidaActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Configurar el botón de detalles
        btnDetalles.setOnClickListener(view -> {
            Intent intent2 = new Intent(BebidaActivity.this, OrdenActivity.class);
            intent2.putExtra("productoslista", (ArrayList<Producto>) bebidasSeleccionadas); // Pasa la lista de productos seleccionados
            startActivity(intent2);
        });

        // Configurar el SearchView para filtrar bebidas
        SearchView busqueda = findViewById(R.id.busqueda);
        busqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No es necesario realizar nada al enviar la búsqueda
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarBebidas(newText); // Llama al método para filtrar
                return true;
            }
        });
    }

    /**
     * Inicializa las vistas de la actividad.
     */
    private void initializeViews() {
        txtTitulo = findViewById(R.id.txtTitulo);
        txtCantidad = findViewById(R.id.txtCantidad);
        recyclerView = findViewById(R.id.recyclerViewBebidas);
        btnDetalles = findViewById(R.id.btnDetalles);
    }

    /**
     * Configura los datos de las bebidas y actualiza el título de la actividad.
     */
    private void setupBebidaList() {
        // Configuración inicial de bebidas
    }

    /**
     * Filtra las bebidas según el texto ingresado en el SearchView.
     *
     * @param texto El texto ingresado en el SearchView.
     */
    private void filtrarBebidas(String texto) {
        List<Producto> bebidasFiltradas = new ArrayList<>();

        // Filtrar la lista de bebidas
        for (Producto bebida : productoList ){
            if (bebida.getNombreProducto().toLowerCase().contains(texto.toLowerCase().trim())){
                bebidasFiltradas.add(bebida);
            }
        }

        // Actualizar el adaptador con la lista filtrada
        bebidaAdapter.actualizarLista(bebidasFiltradas);
    }

    /**
     * Método que se llama desde el adaptador para actualizar la lista de bebidas seleccionadas.
     *
     * @param bebida Producto cuyo estado de cantidad ha cambiado.
     */
    public void actualizarBebidasSeleccionadas(Producto bebida) {
        if (bebida.getCantidad() > 0) {
            if (!bebidasSeleccionadas.contains(bebida)) {
                bebidasSeleccionadas.add(bebida);
            }
        } else {
            bebidasSeleccionadas.remove(bebida);
        }

        // Actualiza el texto de cantidad para reflejar el total en bebidasSeleccionadas
        txtCantidad.setText("Total de productos seleccionados: " + bebidasSeleccionadas.size());
    }
}