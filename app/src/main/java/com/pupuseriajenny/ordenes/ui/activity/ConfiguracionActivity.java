package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pupuseriajenny.ordenes.R;

public class ConfiguracionActivity extends AppCompatActivity {
    private EditText etUrlApi;
    private Button btnGuardar;
    private TextView tvInstruccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);


        // Inicializar vistas
        etUrlApi = findViewById(R.id.et_url_api);
        btnGuardar = findViewById(R.id.btn_guardar);

        tvInstruccion = findViewById(R.id.tv_instruccion);

        // Guardar la URL en SharedPreferences
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etUrlApi.getText().toString();
                if (!url.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("API_URL", url);
                    editor.apply();
                    Toast.makeText(ConfiguracionActivity.this, "URL guardada con éxito", Toast.LENGTH_SHORT).show();
                    etUrlApi.setText("");
                } else {
                    Toast.makeText(ConfiguracionActivity.this, "Por favor, ingresa una URL", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}