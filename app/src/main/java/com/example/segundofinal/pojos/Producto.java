package com.example.segundofinal.pojos;

import java.io.Serializable;

import lombok.Data;

@Data
public class Producto implements Serializable {
    private String codigo;
    private String nombre;
    private Double precio;
    private Integer existencia;
}
