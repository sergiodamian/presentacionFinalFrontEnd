package com.example.segundofinal.pojos;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class VentaCabecera implements Serializable {
    private String id;
    private Date fecha;
    private String nroFactura;
    private Cliente cliente;
    private Double total;
}
