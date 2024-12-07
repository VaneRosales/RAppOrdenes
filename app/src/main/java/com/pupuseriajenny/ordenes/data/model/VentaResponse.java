package com.pupuseriajenny.ordenes.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public class VentaResponse {

    private int idVenta; // El ID de la venta recién creada
    private int idDetalleVenta; // El ID de los detalles de la venta, si es necesario
    private String mensaje; // Un mensaje opcional si la API retorna algo adicional

    // Constructor
    public VentaResponse(int idVenta, int idDetalleVenta, String mensaje) {
        this.idVenta = idVenta;
        this.idDetalleVenta = idDetalleVenta;
        this.mensaje = mensaje;
    }

    // Getters y setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}