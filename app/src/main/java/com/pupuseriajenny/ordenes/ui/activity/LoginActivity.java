package com.pupuseriajenny.ordenes.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pupuseriajenny.ordenes.DTOs.LoginModel;
import com.pupuseriajenny.ordenes.DTOs.LoginResponse;
import com.pupuseriajenny.ordenes.R;
import com.pupuseriajenny.ordenes.RetrofitClient;
import com.pupuseriajenny.ordenes.data.service.AuthService;
import com.pupuseriajenny.ordenes.utils.JWTUtil;
import com.pupuseriajenny.ordenes.utils.TokenUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private AuthService authService;
    private EditText edUser, edPassword;
    private Button btnLogin,btnConfig;
    private TokenUtil tokenUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
btnConfig = findViewById(R.id.btnConfig);
        // Inicializar componentes de la interfaz
        edUser = findViewById(R.id.edUsuario);
        edPassword = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.loginButton);

        // Crear instancia del servicio de autenticación
        authService = RetrofitClient.getClient(this).create(AuthService.class);
        tokenUtil = new TokenUtil(this);


        // Verificación de expiración del token (si el token está presente)
        String token = tokenUtil.getToken();
        if (token != null) {
            Date fechaExpiracion = JWTUtil.obtenerFechaExpiracionDesdeJWT(token);
            Date ahora = new Date();
            if (fechaExpiracion != null && fechaExpiracion.before(ahora)) {
                tokenUtil.removeToken(); // Eliminar token si ha expirado
                Toast.makeText(this, "El token ha expirado. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
        // Verificar si el token es válido al inicio
        if (tokenUtil.isTokenValid()) {
            // Si el token existe y es válido, redirigir a la actividad principal
            Intent intent = new Intent(LoginActivity.this, HistorialActivity.class);
            startActivity(intent);
            finish(); // Terminamos la actividad de login para evitar que el usuario regrese
            return; // Salir de la función onCreate
        }
btnConfig.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(LoginActivity.this,ConfiguracionActivity.class);
        startActivity(intent);
    }
});
        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUser.getText().toString().trim();
                String password = edPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(username, password); // Llamar al método de inicio de sesión
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        LoginModel loginModel = new LoginModel(username, password);
        Call<LoginResponse> call = authService.login(loginModel);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    Log.d("AuthToken", "Token recibido: " + token);
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                    // Guardar el token para futuras solicitudes
                    tokenUtil.saveToken(token);

                    // Redirigir a la actividad principal
                    Intent intent = new Intent(LoginActivity.this, HistorialActivity.class);
                    startActivity(intent);
                    finish(); // Terminamos la actividad de login para evitar que el usuario regrese
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginError", "Fallo en la llamada: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Error en la conexión. Intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
