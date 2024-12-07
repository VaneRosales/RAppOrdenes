package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pupuseriajenny.ordenes.ApiService;
import com.pupuseriajenny.ordenes.AuthManager.AuthManager;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.data.model.Orden;
import com.pupuseriajenny.ordenes.ui.adapter.HistorialAdapter;
import com.pupuseriajenny.ordenes.utils.JWTUtil;
import com.pupuseriajenny.ordenes.utils.TokenUtil;

import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Collections;
import java.util.Comparator;

public class HistorialActivity extends AppCompatActivity implements HistorialAdapter.OnOrdenInteractionListener {

    private TextView tvUsuario;
    private RecyclerView recyclerView;
    private HistorialAdapter historialAdapter;
    private List<Orden> listaOrdenes = new ArrayList<>();
    private ApiService apiService;
    private Button btnAgregar,btnCerrar;
    private static final int EDITAR_ORDEN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        AuthManager authManager = new AuthManager(this);

        // Verificar la expiración del token cuando la actividad se inicia
        authManager.verificarExpiracionDelToken();
        // Inicializar vistas

        tvUsuario = findViewById(R.id.tvUsuario);
        btnAgregar = findViewById(R.id.btnNueva);
        recyclerView = findViewById(R.id.recyclerViewHistorial);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el nombre del usuario desde el token
        TokenUtil tokenUtil = new TokenUtil(this);
        tvUsuario.setText(JWTUtil.obtenerNombre(tokenUtil.getToken()));

        // Crear instancia del servicio API
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Obtener las órdenes desde la API
        obtenerOrdenes();

        // Acción del botón "Nueva Orden"
        btnAgregar.setOnClickListener(view -> {
            Intent intent = new Intent(HistorialActivity.this, HomeActivity.class);
            startActivity(intent);
        });
     /*   btnCerrar.setOnClickListener(view -> {
authManager.cerrarSesion();
        });*/
    }

    // Método para obtener las órdenes desde la API
    private void obtenerOrdenes() {
        apiService.obtenerOrdenes().enqueue(new Callback<List<Orden>>() {
            @Override
            public void onResponse(Call<List<Orden>> call, Response<List<Orden>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaOrdenes = response.body();

                    // Ordenar la lista de órdenes por fecha (más reciente primero)
                    Collections.sort(listaOrdenes, new Comparator<Orden>() {
                        @Override
                        public int compare(Orden o1, Orden o2) {
                            try {
                                // Parsear las fechas y ordenarlas
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                Date date1 = inputFormat.parse(o1.getFechaOrden());
                                Date date2 = inputFormat.parse(o2.getFechaOrden());
                                return date2.compareTo(date1); // Más reciente primero
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0; // Si hay error, no cambiar el orden
                            }
                        }
                    });

                    // Configurar el adaptador con los datos ordenados
                    if (historialAdapter == null) {
                        historialAdapter = new HistorialAdapter(listaOrdenes, HistorialActivity.this);
                        recyclerView.setAdapter(historialAdapter);
                    } else {
                        historialAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast("Error al obtener las órdenes.");
                }
            }

            @Override
            public void onFailure(Call<List<Orden>> call, Throwable t) {
                showToast("Error de red: " + t.getMessage());
            }
        });
    }

    // Mostrar mensajes con Toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Método para cancelar una orden
    public void cancelarOrden(Orden orden) {
        // Primero, crear el objeto de la orden con el nuevo estado


        orden.setEstadoOrden("Cancelada");  // Actualizamos el estado a "Cancelada"

        // Llamamos al servicio API para actualizar el estado de la orden
        apiService.actualizarEstadoOrden(orden.getIdOrden(), orden).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Orden cancelada exitosamente");
                    // Actualizar la lista de órdenes después de la cancelación
                    obtenerOrdenes();
                } else {
                    tvUsuario.setText(response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Error de red: " + t.getMessage());
            }
        });
    }

    // Implementación de la interfaz OnOrdenInteractionListener para editar y eliminar órdenes
    @Override
    public void onEditarOrden(Orden orden) {

        new AlertDialog.Builder(this)
                .setTitle("Editar Orden")
                .setMessage("¿Desea editar la orden de " + orden.getClienteOrden() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Crear un Intent para enviar a EditarOrdenActivity
                    Intent intent = new Intent(HistorialActivity.this, EditarOrdenActivity.class);

                    // Pasar los datos de la orden (ID, cliente, fecha, etc.)
                    intent.putExtra("idOrden", orden.getIdOrden());
                    intent.putExtra("idMesa", orden.getIdMesa());
                    intent.putExtra("clienteOrden", orden.getClienteOrden());
                    intent.putExtra("fechaOrden", orden.getFechaOrden());
                    intent.putExtra("tipoOrden", orden.getTipoOrden());
                    intent.putExtra("estadoOrden", orden.getEstadoOrden());
                    intent.putExtra("comentarioOrden", orden.getComentarioOrden());

                    // Iniciar la actividad de edición
                    startActivity(intent);
                })
                .setNegativeButton("No", null)  // Si el usuario elige "No", no hacer nada
                .show();
    }

    @Override
    public void onEliminarOrden(Orden orden) {
        // Diálogo de confirmación para eliminar
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Orden")
                .setMessage("¿Desea cancelar la orden de " + orden.getClienteOrden() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {

                    // Llamar al método de cancelación de orden al eliminarla
                    cancelarOrden(orden);
                    obtenerOrdenes();
                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITAR_ORDEN_REQUEST && resultCode == RESULT_OK && data != null) {
            int idOrden = data.getIntExtra("idOrden", -1);
            int idMesa = data.getIntExtra("idMesa", -1);
            String cliente = data.getStringExtra("clienteOrden");
            String fecha = data.getStringExtra("fechaOrden");
            String tipo = data.getStringExtra("tipoOrden");
            String estado = data.getStringExtra("estadoOrden");
            String comentario = data.getStringExtra("comentarioOrden");

            for (Orden orden : listaOrdenes) {
                if (orden.getIdOrden() == idOrden) {
                    orden.setIdMesa(idMesa);
                    orden.setClienteOrden(cliente);
                    orden.setFechaOrden(fecha);
                    orden.setTipoOrden(tipo);
                    orden.setEstadoOrden(estado);
                    orden.setComentarioOrden(comentario);
                    break;
                }
            }

            historialAdapter.notifyDataSetChanged();
        }
    }


}

