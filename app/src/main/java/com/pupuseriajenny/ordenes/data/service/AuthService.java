package com.pupuseriajenny.ordenes.data.service;

import com.pupuseriajenny.ordenes.DTOs.LoginModel;
import com.pupuseriajenny.ordenes.DTOs.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthService {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginModel loginModel);

    @POST("api/auth/renew-token") // Suponiendo que esta es la ruta para renovar el token
    Call<LoginResponse> renovarToken(@Header("Authorization") String token);
}