package com.pupuseriajenny.ordenes.data.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RG_Orden {
    // Getters y Setters
    private int idOrden;               // ID de la orden
    private int idMesa;                // ID de la mesa
    private String clienteOrden;       // Nombre del cliente
    private String fechaOrden;         // Fecha de la orden
    private String tipoOrden;          // Tipo de orden (Llevar o Comer en restaurante)
    private String estadoOrden;        // Estado de la orden (Pendiente, Cancelada, Pagada)
    private String comentarioOrden;    // Comentarios adicionales

    // Constructor vacío (necesario para la serialización y deserialización de Retrofit)
    public RG_Orden() {}

    // Constructor con parámetros (si necesitas crear instancias de manera más directa)
    public RG_Orden(int idMesa, String clienteOrden, String fechaOrden, String tipoOrden, String estadoOrden, String comentarioOrden) {
        this.idMesa = idMesa;
        this.clienteOrden = clienteOrden;
        this.fechaOrden = fechaOrden;
        this.tipoOrden = tipoOrden;
        this.estadoOrden = estadoOrden;
        this.comentarioOrden = comentarioOrden;
    }

}