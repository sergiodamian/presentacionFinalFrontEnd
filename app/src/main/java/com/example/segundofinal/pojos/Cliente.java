package com.example.segundofinal.pojos;

import java.io.Serializable;

import lombok.Data;


@Data
public class Cliente implements Serializable {
    private String ruc;
    private String nombres;
    private String email;
}
