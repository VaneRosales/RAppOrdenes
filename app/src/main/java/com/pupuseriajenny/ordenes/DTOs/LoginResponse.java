package com.pupuseriajenny.ordenes.DTOs;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    // Define el campo 'token' que debe coincidir con el nombre en la respuesta JSON de la API
    @SerializedName("token")
    private String token;

    // Getter para el campo 'token'
    public String getToken() {
        return token;
    }

    // Setter para el campo 'token'
    public void setToken(String token) {
        this.token = token;
    }
}
