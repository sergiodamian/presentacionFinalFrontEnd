package com.example.segundofinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button_productos)
    Button buttonProductos;

    @BindView(R.id.button_clientes)
    Button buttonClientes;

    @BindView(R.id.button_ventas)
    Button buttonVentas;

    @BindView(R.id.button_reporte_ventas)
    Button buttonReporteVentas;

    @BindView(R.id.button_ventas_detallado)
    Button buttonReporteVentasDetallado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initBtnClientes();
        initBtnProductos();
        initBtnNuevaVenta();
        initBtnReporteResumido();
        initBtnReporteDetallado();
    }

    private void initBtnProductos() {
        buttonProductos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ABMProductosActivity.class);
            startActivity(intent);
        });
    }

    private void initBtnClientes() {
        buttonClientes.setOnClickListener(v -> {
            Intent intent = new Intent(this, ABMClientesActivity.class);
            startActivity(intent);
        });
    }

    private void initBtnNuevaVenta() {
        buttonVentas.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevaVentaActivity.class);
            startActivity(intent);
        });
    }

    private void initBtnReporteDetallado() {
        buttonReporteVentasDetallado.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerReporteDetalladoActivity.class);
            startActivity(intent);
        });
    }


    private void initBtnReporteResumido() {
        buttonReporteVentas.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerReporteResumidoActivity.class);
            startActivity(intent);
        });
    }

}