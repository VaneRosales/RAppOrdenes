package com.pupuseriajenny.ordenes.data.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Bebida  implements Serializable {
    // Getters y setters
    private String nombreProducto;
    private double precioProducto;
    private int imagenResourceId;
    private int cantidad;
    private double costoUnitarioProducto;
    private int idCategoria;

    private int        idProveedor;

    public Bebida(String nombreProducto, double precioProducto,double costoUnitarioProducto,int idCategoria,int idProveedor) {
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.idCategoria = idCategoria;
        this.imagenResourceId = imagenResourceId;
        this.cantidad = 0; // Valor inicial de cantidad
    }


}
