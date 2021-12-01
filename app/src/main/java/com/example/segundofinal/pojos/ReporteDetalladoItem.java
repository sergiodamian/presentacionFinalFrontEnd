package com.example.segundofinal.pojos;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ReporteDetalladoItem implements Serializable {
    private Cliente cliente;
    private Date fecha;
    private Producto producto;
    private Integer cantidad;
    private Double total;
}
