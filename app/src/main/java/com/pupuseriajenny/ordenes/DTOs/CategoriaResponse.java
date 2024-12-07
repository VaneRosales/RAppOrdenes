package com.pupuseriajenny.ordenes.DTOs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
public class CategoriaResponse {
    private List<String> categoria;

    // Getter y Setter
    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }
}
