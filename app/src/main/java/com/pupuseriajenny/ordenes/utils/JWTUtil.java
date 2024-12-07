package com.pupuseriajenny.ordenes.utils;

import com.auth0.android.jwt.JWT;

import java.util.Date;

public class JWTUtil {

    // Método para obtener el idEmpleado desde el JWT
    public static int obtenerIdEmpleadoDesdeJWT(String token) {
        try {
            JWT jwt = new JWT(token);  // Decodificamos el JWT
            // Obtener el ID del empleado del claim "idEmpleado"
            return jwt.getClaim("idEmpleado").asInt();  // Cambiar "idEmpleado" si es necesario
        } catch (Exception e) {
            // Si ocurre un error, capturamos la excepción y retornamos un valor predeterminado
            e.printStackTrace();
            return -1;  // Retornar -1 si no se puede obtener el ID
        }
    }public static Date obtenerFechaExpiracionDesdeJWT(String token) {
        try {
            JWT jwt = new JWT(token);  // Decodificamos el JWT
            // Obtener la fecha de expiración (exp) como un objeto Date
            long exp = jwt.getClaim("exp").asLong(); // El valor de "exp" es un timestamp en segundos
            return new Date(exp * 1000);  // Convertir a milisegundos
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Retornar null si no se puede obtener la fecha
        }
    }public static String obtenerNombre(String token) {
        try {
            JWT jwt = new JWT(token); // Decodificamos el JWT
            String nombre = jwt.getClaim("nombreEmpleado").asString();
            String apellido = jwt.getClaim("apellidoEmpleado").asString();
            String cargo = jwt.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString();

            // Formatear la información
            return String.format("Nombre completo: %s %s, Cargo: %s", nombre, apellido, cargo);
        } catch (Exception e) {
            e.printStackTrace();
            return "Información no disponible"; // Manejo de error
        }
    }

    // Método para obtener cualquier claim del JWT de manera reutilizable
    public static String obtenerClaimDesdeJWT(String token, String claimName) {
        try {
            JWT jwt = new JWT(token);  // Decodificamos el JWT
            // Obtener el claim solicitado
            return jwt.getClaim(claimName).asString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Retornar null si no se puede obtener el claim
        }
    }
}