package com.pupuseriajenny.ordenes.data.model;

import lombok.Data;

@Data

public class Empleado {
   
    private int idEmpleado;
    private String nombresEmpleado;
    private String apellidosEmpleado;
    private String telefono;
    private String direccion;
    private String email;
    private String fechaNacimiento;
    private int idCargo;
}
