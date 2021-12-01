package com.example.segundofinal.pojos;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;


@Data
public class Venta implements Serializable {
    private VentaCabecera cabecera;
    private List<VentaDetalle> detalles;

    public Venta() {
        this.detalles = new LinkedList<>();
    }
}
