package com.pupuseriajenny.ordenes.ui.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pupuseriajenny.ordenes.ApiService;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.data.model.Orden;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarOrdenActivity extends AppCompatActivity {
    private ApiService apiService;
    private EditText edtCliente,edtMesa, edtFecha, edtComentario;
    private Spinner spinnerTipo, spinnerEstado;
    private Button btnGuardar;
    private int idOrden, idMesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_orden);

        // Obtener los datos pasados por Intent
        idOrden = getIntent().getIntExtra("idOrden", -1);
        idMesa = getIntent().getIntExtra("idMesa", -1);
        String clienteOrden = getIntent().getStringExtra("clienteOrden");
        String fechaOrden = getIntent().getStringExtra("fechaOrden");
        String tipoOrden = getIntent().getStringExtra("tipoOrden");
        String estadoOrden = getIntent().getStringExtra("estadoOrden");
        String comentarioOrden = getIntent().getStringExtra("comentarioOrden");

        // Asignar los valores a los campos de la UI
        edtCliente = findViewById(R.id.edtCliente);
        edtMesa=findViewById(R.id.edtMesa);
        edtFecha = findViewById(R.id.edtFecha);
        edtComentario = findViewById(R.id.edtComentario);

        // Spinners para Tipo y Estado de la Orden
        spinnerTipo = findViewById(R.id.spinnerTipoOrden);
        spinnerEstado = findViewById(R.id.spinnerEstadoOrden);

        // Configurar los Spinners con los valores de strings.xml
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this, R.array.tipo_orden, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this, R.array.estado_orden, android.R.layout.simple_spinner_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        // Establecer los valores seleccionados en los Spinners
        setSpinnerSelection(spinnerTipo, tipoOrden, R.array.tipo_orden);
        setSpinnerSelection(spinnerEstado, estadoOrden, R.array.estado_orden);

        edtCliente.setText(clienteOrden);
        edtMesa.setText(idMesa != -1 ? String.valueOf(idMesa) : "");
        edtComentario.setText(comentarioOrden);

        // Cargar la fecha inicial en el EditText
        edtFecha.setText(fechaOrden);

        // Configurar el DatePickerDialog al hacer clic en el icono del calendario
        ImageView imgCalendar = findViewById(R.id.imgCalendar);
        imgCalendar.setOnClickListener(v -> showDatePickerDialog());

        // Botón para guardar los cambios
        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            // Recoger los datos editados por el usuario
            String nuevoCliente = edtCliente.getText().toString();
            String nuevaMesa = edtMesa.getText().toString();
            String nuevaFecha = edtFecha.getText().toString();
            String nuevoTipo = spinnerTipo.getSelectedItem().toString();
            String nuevoEstado = spinnerEstado.getSelectedItem().toString();
            String nuevoComentario = edtComentario.getText().toString();

            // Validaciones antes de continuar
            if (validarCampos(nuevoCliente, nuevaMesa, nuevaFecha, nuevoTipo, nuevoEstado)) {
                // Actualizar la orden en la API
                int idMesaActualizado = Integer.parseInt(nuevaMesa);
                actualizarOrden(idOrden,idMesa, nuevoCliente, nuevaFecha, nuevoTipo, nuevoEstado, nuevoComentario);
            }
        });
    }

    private boolean validarCampos(String cliente,String mesa, String fecha, String tipo, String estado) {
        if (cliente.isEmpty()) {
            edtCliente.setError("El cliente es obligatorio");
            return false;
        }
        if (mesa.isEmpty() || !mesa.matches("\\d+")) { // Verifica que sea un número
            edtMesa.setError("La mesa debe ser un número válido");
            return false;
        }
        if (fecha.isEmpty()) {
            edtFecha.setError("La fecha es obligatoria");
            return false;
        }
        if (tipo.equals("Seleccionar tipo")) {
            Toast.makeText(this, "Selecciona un tipo de orden", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (estado.equals("Seleccionar estado")) {
            Toast.makeText(this, "Selecciona un estado de orden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    String fechaSeleccionada = sdf.format(selectedDate.getTime());

                    edtFecha.setText(fechaSeleccionada);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void actualizarOrden(int idOrden,int idMesa, String cliente, String fecha, String tipo, String estado, String comentario) {
        Orden ordenActualizada = new Orden();
        ordenActualizada.setIdOrden(idOrden);
        ordenActualizada.setClienteOrden(cliente);
        ordenActualizada.setIdMesa(idMesa);
        ordenActualizada.setFechaOrden(fecha);
        ordenActualizada.setTipoOrden(tipo);
        ordenActualizada.setEstadoOrden(estado);
        ordenActualizada.setComentarioOrden(comentario);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.actualizarEstadoOrden(idOrden, ordenActualizada).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    devolverDatos(cliente,idMesa, fecha, tipo, estado, comentario);
                } else {
                    // Mostrar detalles del error en los logs
                    Log.e("API_ERROR", "Código de respuesta: " + response.code());
                    Log.e("API_ERROR", "Error del servidor: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_ERROR", "Cuerpo del error: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(EditarOrdenActivity.this, "Error al actualizar la orden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditarOrdenActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void devolverDatos(String cliente, int idMesa, String fecha, String tipo, String estado, String comentario) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("idOrden", idOrden);
        resultIntent.putExtra("idMesa", idMesa); // Pasar idMesa como entero
        resultIntent.putExtra("clienteOrden", cliente);
        resultIntent.putExtra("fechaOrden", fecha);
        resultIntent.putExtra("tipoOrden", tipo);
        resultIntent.putExtra("estadoOrden", estado);
        resultIntent.putExtra("comentarioOrden", comentario);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setSpinnerSelection(Spinner spinner, String value, int arrayResId) {
        // Obtener el arreglo de recursos
        String[] array = getResources().getStringArray(arrayResId);

        // Inicializar la posición con -1 (por si no se encuentra el valor)
        int position = -1;

        // Buscar la posición del valor en el arreglo
        for (int i = 0; i < array.length; i++) {
            if (array[i].trim().equalsIgnoreCase(value.trim())) {
                position = i;
                break;
            }
        }

        // Si la posición es válida, establece la selección; de lo contrario, selecciona el primer elemento
        if (position >= 0) {
            spinner.setSelection(position);

        } else {
            spinner.setSelection(0); // Establece una selección predeterminada
        }
    }
}





