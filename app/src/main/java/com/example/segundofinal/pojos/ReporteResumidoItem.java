package com.example.segundofinal.pojos;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ReporteResumidoItem implements Serializable {
    private Cliente cliente;
    private Date fecha;
    private Venta venta;
    private String factura;
    private Double total;
}
