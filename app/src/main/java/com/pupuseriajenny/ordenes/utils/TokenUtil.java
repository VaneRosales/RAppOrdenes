package com.pupuseriajenny.ordenes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenUtil {

    private static final String PREFS_NAME = "secret_shared_prefs";  // Nombre del archivo de SharedPreferences
    private SharedPreferences sharedPreferences;
    private Context context;

    // Constructor para inicializar el contexto y configurar EncryptedSharedPreferences
    public TokenUtil(Context context) {
        this.context = context;

        try {
            // Crear o recuperar la clave maestra para el cifrado
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Crear o abrir EncryptedSharedPreferences
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREFS_NAME,  // Nombre del archivo de SharedPreferences
                    masterKeyAlias,  // Alias de la clave maestra
                    context,  // Contexto de la aplicación
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,  // Esquema de cifrado de claves
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // Esquema de cifrado de valores
            );
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al crear SharedPreferences cifradas", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para guardar el token cifrado
    public void saveToken(String token) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jwt_token", token);  // Guardamos el token cifrado
            editor.apply();  // Aplicamos los cambios
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar el token cifrado", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isTokenValid() {
        String token = getToken();
        return token != null;
    }
    // Método para obtener el token descifrado
    public String getToken() {
        try {
            return sharedPreferences.getString("jwt_token", null);  // Recuperamos el token cifrado
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al obtener el token cifrado", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public void removeToken() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("jwt_token");  // Eliminamos el token
            editor.apply();  // Aplicamos los cambios
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al eliminar el token", Toast.LENGTH_SHORT).show();
        }
    }
}
