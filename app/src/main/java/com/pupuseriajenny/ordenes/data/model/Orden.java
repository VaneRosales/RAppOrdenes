package com.pupuseriajenny.ordenes.data.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Orden {
    // Getters y setters
    private int idOrden;
    private int idMesa;
    private String clienteOrden;
    private String fechaOrden;
    private String tipoOrden;
    private String estadoOrden;
    private String comentarioOrden;

}
