package com.example.segundofinal.pojos;

import java.io.Serializable;

import lombok.Data;

@Data
public class VentaDetalle implements Serializable {
    private Producto producto;
    private Integer cantidad;
    private Double subtotal;
}
