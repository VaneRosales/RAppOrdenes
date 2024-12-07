package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pupuseriajenny.ordenes.data.model.DetallesVentas;
import com.pupuseriajenny.ordenes.data.model.DetallesVentasResponse;
import com.pupuseriajenny.ordenes.data.model.Orden;
import com.pupuseriajenny.ordenes.data.model.Producto;
import com.pupuseriajenny.ordenes.data.model.RG_Orden;
import com.pupuseriajenny.ordenes.data.model.Venta;
import com.pupuseriajenny.ordenes.ui.adapter.BebidaAdapter;
import com.pupuseriajenny.ordenes.ui.listener.BebidaActionsListener;
import com.pupuseriajenny.ordenes.utils.JWTUtil;
import com.pupuseriajenny.ordenes.utils.TokenUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdenActivity extends AppCompatActivity implements BebidaActionsListener {
    private Spinner spnTipoOrden ;
    private ArrayList<Producto> bebidasSeleccionadas;
    private Button btnEnviar, btnCancelar;
    private TextView txtTotal;
    private RecyclerView recyclerView;
    private float totalVenta;
    private BebidaAdapter bebidaAdapter;
private EditText edtCliente;
private EditText  edtMesa;
private EditText edtComentario;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orden);
        // Crear instancia de AuthManager
        AuthManager authManager = new AuthManager(this);

        // Verificar la expiración del token cuando la actividad se inicia
        authManager.verificarExpiracionDelToken();
        // Inicializar vistas
        spnTipoOrden = findViewById(R.id.spnTipoOrden);
        btnEnviar = findViewById(R.id.btnEnviar);
        edtComentario = findViewById(R.id.edtComentario);
        edtMesa = findViewById(R.id.edtMesa);
        edtCliente = findViewById(R.id.edtCliente);
        btnCancelar = findViewById(R.id.btnCancelar);
        txtTotal = findViewById(R.id.txtCantidad);
        recyclerView = findViewById(R.id.recyclerViewBebidas);

        // Obtener el token JWT y crear la instancia de ApiService
         apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Configurar el botón de cancelar
        btnCancelar.setOnClickListener(view -> {
            Intent intent = new Intent(this, HistorialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Obtener las bebidas seleccionadas del intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("productoslista")) {
            bebidasSeleccionadas = (ArrayList<Producto>) intent.getSerializableExtra("productoslista");
            if (bebidasSeleccionadas != null) {
                setupRecyclerView();
                actualizarTotal();
            }
        }

        // Configurar el botón de enviar
        btnEnviar.setOnClickListener(view -> crearOrden());

        // Configurar los márgenes para las barras del sistema (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bebidaAdapter = new BebidaAdapter(bebidasSeleccionadas, this);
        recyclerView.setAdapter(bebidaAdapter);
    }

    private void crearOrden() {
        Orden orden = new Orden();

        try {
            // Convertir el texto a un entero
            int idMesa = Integer.parseInt(edtMesa.getText().toString());

            // Asignar el valor a la orden
            orden.setIdMesa(idMesa);
        } catch (NumberFormatException e) {
            // Manejo de error si el texto no es un número válido
            Toast.makeText(this, "Por favor ingresa un número válido para la mesa", Toast.LENGTH_SHORT).show();
            return; // Salir del método si la conversión falla
        }

        String nombreCliente = edtCliente.getText().toString();

        // Validar que el campo cliente no esté vacío
        if (nombreCliente.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        orden.setClienteOrden(nombreCliente);

        // Configurar el formato de la fecha y hora
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String fechaFormateada = dateFormat.format(new Date());
        orden.setFechaOrden(fechaFormateada);

        String tipoOrden = spnTipoOrden.getSelectedItem().toString();
        orden.setTipoOrden(tipoOrden);
        orden.setEstadoOrden("Pendiente");
        orden.setComentarioOrden(edtComentario.getText().toString());

        // Llamada para crear la orden
        apiService.insertarOrden(orden).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    insertarDetallesVenta(response.body()); // Usamos el id de la orden
                } else {
                    showToast("Error al crear la orden");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                showToast("Error de red: " + t.getMessage());
            }
        });
    }


    private void insertarDetallesVenta(int idOrden) {
        for (Producto producto : bebidasSeleccionadas) {
            DetallesVentas detalleVenta = new DetallesVentas();
            detalleVenta.setIdProducto(producto.getIdProducto());
            detalleVenta.setIdOrden(idOrden);
            detalleVenta.setCantidadDetalleVenta(producto.getCantidad());
            detalleVenta.setSubTotalDetalleVenta(producto.getPrecioProducto() * producto.getCantidad());

            // Insertar detalles de venta
            apiService.insertarDetalleVenta(detalleVenta).enqueue(new Callback<DetallesVentasResponse>() {
                @Override
                public void onResponse(Call<DetallesVentasResponse> call, Response<DetallesVentasResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        crearVenta(response.body().idDetalleVenta);
                    } else {
                        showToast("Error al insertar detalle de venta");
                    }
                }

                @Override
                public void onFailure(Call<DetallesVentasResponse> call, Throwable t) {
                    showToast("Error de red: " + t.getMessage());
                }
            });
        }Intent intent = new Intent(this,HistorialActivity.class);
        startActivity(intent);
    }

    private void crearVenta(int idDetalle) {
        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdDetalleVenta(idDetalle);
        nuevaVenta.setTotalVenta(totalVenta);
        TokenUtil tokenUtil = new TokenUtil(this);
        nuevaVenta.setIdEmpleado(JWTUtil.obtenerIdEmpleadoDesdeJWT(tokenUtil.getToken()));

        // Llamada para crear la venta
        apiService.insertarVenta(nuevaVenta).enqueue(new Callback<Venta>() {
            @Override
            public void onResponse(Call<Venta> call, Response<Venta> response) {
                if (response.isSuccessful()) {
                    showToast("Venta creada correctamente");
                } else {
                    showToast("Error al crear la venta");
                }
            }

            @Override
            public void onFailure(Call<Venta> call, Throwable t) {
                showToast("Error de red: " + t.getMessage());
            }
        });
    }

    private void actualizarTotal() {
        totalVenta = 0;
        for (Producto bebida : bebidasSeleccionadas) {
            totalVenta += bebida.getPrecioProducto() * bebida.getCantidad();
        }
        txtTotal.setText(String.format("Total: %.2f", totalVenta));
    }

    @Override
    public void actualizarBebidasSeleccionadas(Producto bebida) {
        actualizarTotal();
    }

    private void showToast(String message) {
        Toast.makeText(OrdenActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
