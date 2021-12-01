package com.example.segundofinal;

import android.app.Application;

import com.example.segundofinal.pojos.Cliente;
import com.example.segundofinal.pojos.Producto;
import com.example.segundofinal.pojos.ReporteDetalladoItem;
import com.example.segundofinal.pojos.ReporteResumidoItem;
import com.example.segundofinal.pojos.Venta;
import com.example.segundofinal.pojos.VentaCabecera;
import com.example.segundofinal.pojos.VentaDetalle;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    /**
     * Datos en memoria de productos.
     */
    private List<Producto> productos;

    /**
     * Datos en memoria de clientes.
     */
    private List<Cliente> clientes;

    /**
     * Datos de ventas.
     */
    private List<Venta> ventas;

    @Override
    public void onCreate() {
        super.onCreate();
        this.clientes = new LinkedList<>();
        this.productos = new LinkedList<>();
        this.clientes = new LinkedList<>();
        this.ventas = new LinkedList<>();

        mockupProductos();
        mockupClientes();
    }

    private void mockupProductos() {
        for (int i = 0; i < 9; i++) {
            Producto p = new Producto();
            p.setCodigo("P000" + String.valueOf(i + 1));
            p.setNombre("P000" + String.valueOf(i + 1));
            p.setExistencia(i + 1);
            p.setPrecio(1000.0 * (i + 1));
            productos.add(p);
        }
    }

    private void mockupClientes() {
        for (int i = 0; i < 9; i++) {
            Cliente c = new Cliente();
            c.setRuc("C000" + String.valueOf(i + 1));
            c.setNombres("C000" + String.valueOf(i + 1));
            c.setEmail("c000" + String.valueOf(i + 1) + "@mail.com.py");
            clientes.add(c);
        }
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void guardarProducto(Producto producto) {
        productos.add(producto);
    }

    public Producto buscarProducto(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equals(codigo)) {
                return producto;
            }
        }
        return null;
    }

    public void eliminarProducto(String codigo) {
        Iterator<Producto> it = productos.iterator();
        while (it.hasNext()) {
            Producto producto = it.next();
            if (producto.getCodigo().equals(codigo)) {
                it.remove();
            }
        }
    }

    public void editarProducto(String codigo, Producto producto) {
        eliminarProducto(codigo);
        guardarProducto(producto);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void guardarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public Cliente buscarCliente(String ruc) {
        for (Cliente cliente : clientes) {
            if (cliente.getRuc().equals(ruc)) {
                return cliente;
            }
        }
        return null;
    }

    public void eliminarCliente(String ruc) {
        Iterator<Cliente> it = clientes.iterator();
        while (it.hasNext()) {
            Cliente cliente = it.next();
            if (cliente.getRuc().equals(ruc)) {
                it.remove();
            }
        }
    }

    public void editarCliente(String ruc, Cliente cliente) {
        eliminarCliente(ruc);
        guardarCliente(cliente);
    }

    public void registrarVenta(Venta venta) {
        this.ventas.add(venta);
    }

    public List<ReporteDetalladoItem> generarReporteDetallado(String producto, Date fechaDesde,
                                                              Date fechaHasta) {
        boolean noFiltrar = ((producto == null || producto.equals("")) && fechaDesde == null && fechaHasta == null);
        List<ReporteDetalladoItem> items = new LinkedList<>();
        for (Venta venta : ventas) {
            for (VentaDetalle detalle : venta.getDetalles()) {
                if (noFiltrar) {
                    ReporteDetalladoItem item = adapt1(venta, detalle);
                    items.add(item);
                } else {
                    // Filtro de producto.
                    boolean a = ((producto == null || producto.isEmpty()) || (detalle.getProducto().getCodigo().contains(producto) || detalle.getProducto().getNombre().contains(producto)));
                    boolean b = (fechaDesde == null || (fechaDesde != null && venta.getCabecera().getFecha().compareTo(fechaDesde) >= 0));
                    boolean c = (fechaHasta == null || (fechaHasta != null && venta.getCabecera().getFecha().compareTo(fechaHasta) <= 0));
                    if (a && b && c) {
                        ReporteDetalladoItem item = adapt1(venta, detalle);
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    public List<ReporteResumidoItem> generarReporteResumido(String cliente, Date fechaDesde,Date fechaHasta) {
        boolean noFiltrar = ((cliente == null || cliente.equals("")) && fechaDesde == null && fechaHasta == null);
        List<ReporteResumidoItem> items = new LinkedList<>();
        for (Venta venta : ventas) {
            VentaCabecera detalle = venta.getCabecera();
                if (noFiltrar) {
                    ReporteResumidoItem item = adapt2(venta, detalle);
                    items.add(item);
                } else {
                    // Filtro de cliente.
                    boolean a =
                            ((cliente == null || cliente.isEmpty()) || (detalle.getCliente().getRuc().contains(cliente) || detalle.getCliente().getNombres().contains(cliente)));
                    boolean b = (fechaDesde == null || (fechaDesde != null && venta.getCabecera().getFecha().compareTo(fechaDesde) >= 0));
                    boolean c = (fechaHasta == null || (fechaHasta != null && venta.getCabecera().getFecha().compareTo(fechaHasta) <= 0));
                    if (a && b && c) {
                        ReporteResumidoItem item = adapt2(venta, detalle);
                        items.add(item);
                    }
                }
        }

        return items;
    }

    private ReporteDetalladoItem adapt1(Venta venta, VentaDetalle detalle) {
        ReporteDetalladoItem item = new ReporteDetalladoItem();
        item.setCliente(venta.getCabecera().getCliente());
        item.setFecha(venta.getCabecera().getFecha());
        item.setProducto(detalle.getProducto());
        item.setCantidad(detalle.getCantidad());
        item.setTotal(detalle.getSubtotal());
        return item;
    }

    private ReporteResumidoItem adapt2(Venta venta, VentaCabecera detalle) {
        ReporteResumidoItem item = new ReporteResumidoItem();
        item.setCliente(venta.getCabecera().getCliente());
        item.setFecha(venta.getCabecera().getFecha());
        item.setTotal(detalle.getTotal());
        item.setFactura(detalle.getNroFactura());
        return item;
    }

}
