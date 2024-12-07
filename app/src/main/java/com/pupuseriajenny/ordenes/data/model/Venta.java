package com.pupuseriajenny.ordenes.data.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter


public class Venta {
    // Getters y Setters para cada atributo
    private int idVenta;
    private int idEmpleado;
    private int idDetalleVenta;
    private double totalVenta;
    private String fechaVenta;

    // Constructor
    public Venta() {
        this.idVenta = idVenta;
        this.idEmpleado = idEmpleado;
        this.idDetalleVenta = idDetalleVenta;
        this.totalVenta = totalVenta;
        this.fechaVenta = fechaVenta;
    }

}
