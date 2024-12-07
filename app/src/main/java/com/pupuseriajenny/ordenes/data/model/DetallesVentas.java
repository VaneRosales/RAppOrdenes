package com.pupuseriajenny.ordenes.data.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetallesVentas {
    private int idDetalleVenta;
    private int idOrden;
    private int idProducto;
    private int cantidadDetalleVenta;
    private double subTotalDetalleVenta;

    // Constructor vacío (opcional)
    public DetallesVentas() {
    }

    // Getters y Setters

}
