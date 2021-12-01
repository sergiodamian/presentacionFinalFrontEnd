package com.example.segundofinal;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.segundofinal.pojos.Cliente;
import com.example.segundofinal.pojos.Producto;
import com.example.segundofinal.pojos.Venta;
import com.example.segundofinal.pojos.VentaCabecera;
import com.example.segundofinal.pojos.VentaDetalle;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NuevaVentaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static volatile NuevaVentaActivity INSTANCE;

    private Venta venta;

    @BindView(R.id.etId)
    EditText etId;

    @BindView(R.id.etFecha)
    EditText etFecha;

    @BindView(R.id.etFactura)
    EditText etFactura;

    @BindView(R.id.spCliente)
    Spinner spCliente;

    @BindView(R.id.txtTotal)
    TextView txtTotal;

    @BindView(R.id.btnAgregarProducto)
    Button btnAgregarProducto;

    @BindView(R.id.btnConfirmar)
    Button btnConfirmar;

    @BindView(R.id.rvDetalles)
    RecyclerView rvDetalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_venta);
        ButterKnife.bind(this);

        NuevaVentaActivity.INSTANCE = this;

        inicializarDatosVenta();
        inicializarInputFecha();
        inicializarInputCliente();
        inicializarListaDetalles();
        inicializarBtnAgregarProducto();
        inicializarBtnConfirmarVenta();
    }

    private void inicializarDatosVenta() {
        this.venta = new Venta();
        this.venta.setCabecera(new VentaCabecera());
        this.venta.setDetalles(new LinkedList<>());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etFecha.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
    }

    private void inicializarInputFecha() {
        etFecha.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
            int monthOfYear = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);
            String fecha = etFecha.getText().toString();
            if (fecha != null && !fecha.equals("")) {
                String[] parts = fecha.split(Pattern.quote("/"));
                dayOfMonth = Integer.valueOf(parts[0], 10);
                monthOfYear = Integer.valueOf(parts[1], 10) - 1;
                year = Integer.valueOf(parts[2], 10);
            }

            DatePickerDialog dpd = DatePickerDialog.newInstance(NuevaVentaActivity.INSTANCE, year, monthOfYear, dayOfMonth);
            dpd.show(NuevaVentaActivity.INSTANCE.getSupportFragmentManager(), "Datepickerdialog");
        });
    }

    private void inicializarInputCliente() {
        MyApplication app = (MyApplication) getApplication();
        Cliente[] clientes = app.getClientes().toArray(new Cliente[0]);
        SpClientesAdapter adapter = new SpClientesAdapter(this, clientes);
        spCliente.setAdapter(adapter);
        spCliente.setOnItemSelectedListener(adapter);
    }

    private void inicializarListaDetalles() {
        NuevaVentaActivity.RvDetallesAdapter adapter = new NuevaVentaActivity.RvDetallesAdapter();
        rvDetalles.setLayoutManager(new LinearLayoutManager(this));
        rvDetalles.setAdapter(adapter);

        // Inicializar monto total.
        updateTotal();
    }

    private void inicializarBtnAgregarProducto() {
        btnAgregarProducto.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(NuevaVentaActivity.INSTANCE);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_nueva_venta_item_producto);

            // Seleccion de producto.
            final Spinner spProducto = dialog.findViewById(R.id.spProducto);
            MyApplication app = (MyApplication) NuevaVentaActivity.INSTANCE.getApplication();
            Producto[] productos = app.getProductos().toArray(new Producto[0]);
            SpProductosAdapter adapter = new SpProductosAdapter(this, productos);
            spProducto.setAdapter(adapter);
            spProducto.setOnItemSelectedListener(adapter);

            // Cantidad de producto.
            final EditText txtCantidad = dialog.findViewById(R.id.txtCantidad);

            final Button btnAgregarProducto = dialog.findViewById(R.id.btnAgregarProducto);
            btnAgregarProducto.setOnClickListener(v0 -> {
                // Control.
                Producto producto = ((SpProductosAdapter) spProducto.getAdapter()).selection;
                if (producto == null) {
                    Toast.makeText(NuevaVentaActivity.INSTANCE, "Se debe seleccionar un producto", Toast.LENGTH_LONG).show();
                    return;
                }

                String cantidad0 = txtCantidad.getText().toString();
                if (cantidad0 == null || cantidad0.isEmpty()) {
                    Toast.makeText(NuevaVentaActivity.INSTANCE, "Se debe especificar una cantidad", Toast.LENGTH_LONG).show();
                    return;
                }

                Integer cantidad = null;
                try {
                    cantidad = Integer.valueOf(cantidad0, 10);
                    if (cantidad <= 0) {
                        Toast.makeText(NuevaVentaActivity.INSTANCE, "Se debe especificar una cantidad mayor a cero", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (Throwable thr) {
                    Toast.makeText(NuevaVentaActivity.INSTANCE, "Se debe especificar una cantidad numérica", Toast.LENGTH_LONG).show();
                    return;
                }

                // Ok.
                NuevaVentaActivity.INSTANCE.agregarDetalle(producto, cantidad);
            });

            dialog.show();
        });
    }

    private void inicializarBtnConfirmarVenta() {
        btnConfirmar.setOnClickListener(v -> {
            // Validar datos.
            String err = validarInput();
            if (err != null) {
                Toast.makeText(this, err, Toast.LENGTH_LONG).show();
                return;
            }

            // Generar cabecera.
            venta.getCabecera().setId(etId.getText().toString());
            venta.getCabecera().setFecha(obtenerFecha());
            venta.getCabecera().setNroFactura(etFactura.getText().toString());
            venta.getCabecera().setCliente(((SpClientesAdapter) spCliente.getAdapter()).selection);
            venta.getCabecera().setTotal(obtenerTotal());

            // Persistir venta.
            MyApplication app = (MyApplication) getApplication();
            app.registrarVenta(venta);

            // Reinicializar datos.
            inicializarDatosVenta();
            inicializarInputFecha();
            inicializarInputCliente();
            inicializarListaDetalles();

            etId.setText("");
            etFecha.setText("");
            etFactura.setText("");

            Toast.makeText(this, "Nueva Venta Registrada", Toast.LENGTH_LONG).show();
        });
    }

    private String validarInput() {
        // Validar id.
        String id = etId.getText().toString();
        if (id == null || id.isEmpty()) {
            return "Se debe especificar un identificador";
        }

        // Validar fecha.
        String fecha = etFecha.getText().toString();
        if (fecha == null || fecha.isEmpty()) {
            return "Se debe especificar una fecha";
        }

        // Validar nro de factura.
        String factura = etFactura.getText().toString();
        if (factura == null || factura.isEmpty()) {
            return "Se debe especificar un número de factura";
        }

        // Validar cliente.
        SpClientesAdapter adapter = (SpClientesAdapter) spCliente.getAdapter();
        if (adapter.selection == null) {
            return "Se debe especificar un cliente";
        }

        // Validar productos.
        if (venta.getDetalles().isEmpty()) {
            return "Se debe especificar al menos un detalle";
        }

        return null;
    }

    private void agregarDetalle(Producto producto, Integer cantidad) {
        // Aumentar cantidad, si hubiere.
        for (VentaDetalle detalle : venta.getDetalles()) {
            if (detalle.getProducto().getCodigo().equals(producto.getCodigo())) {
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                detalle.setSubtotal(detalle.getCantidad() * detalle.getProducto().getPrecio());
                return;
            }
        }

        // Agregar nuevo detalle.
        VentaDetalle detalle = new VentaDetalle();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(cantidad * producto.getPrecio());
        venta.getDetalles().add(detalle);

        // Actualizar interfaz.
        rvDetalles.getAdapter().notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        Double total = obtenerTotal();
        txtTotal.setText(String.format("TOTAL: %s Gs", total));
    }

    private Double obtenerTotal() {
        Double total = 0.0;
        for (VentaDetalle detalle : venta.getDetalles()) {
            total = total + detalle.getSubtotal();
        }
        return total;
    }

    private Date obtenerFecha() {
        String[] parts = etFecha.getText().toString().split(Pattern.quote("/"));
        int dayOfMonth = Integer.valueOf(parts[0], 10);
        int monthOfYear = Integer.valueOf(parts[1], 10) - 1;
        int year = Integer.valueOf(parts[2], 10);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        now.set(Calendar.MONTH, monthOfYear);
        now.set(Calendar.YEAR, year);
        return now.getTime();
    }

    public static class SpClientesAdapter extends ArrayAdapter<Cliente> implements AdapterView.OnItemSelectedListener {
        private Cliente selection;

        public SpClientesAdapter(@NonNull Context context, Cliente[] clientes) {
            super(context, android.R.layout.simple_spinner_item, clientes);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            this.selection = getItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Cliente cliente = getItem(position);

            View view = super.getView(position, convertView, parent);
            TextView txt = view.findViewById(android.R.id.text1);
            txt.setText(String.format("%s (%s)", cliente.getNombres(), cliente.getRuc()));

            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Cliente cliente = getItem(position);

            View view = super.getDropDownView(position, convertView, parent);
            TextView txt = view.findViewById(android.R.id.text1);
            txt.setText(String.format("%s (%s)", cliente.getNombres(), cliente.getRuc()));

            return view;
        }
    }

    public static class SpProductosAdapter extends ArrayAdapter<Producto> implements AdapterView.OnItemSelectedListener {
        private Producto selection;

        public SpProductosAdapter(@NonNull Context context, Producto[] clientes) {
            super(context, android.R.layout.simple_spinner_item, clientes);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            this.selection = getItem(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            ;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Producto producto = getItem(position);

            View view = super.getView(position, convertView, parent);
            TextView txt = view.findViewById(android.R.id.text1);
            txt.setText(String.format("%s (%s)", producto.getNombre(), producto.getCodigo()));

            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Producto producto = getItem(position);

            View view = super.getDropDownView(position, convertView, parent);
            TextView txt = view.findViewById(android.R.id.text1);
            txt.setText(String.format("%s (%s)", producto.getNombre(), producto.getCodigo()));

            return view;
        }
    }

    public static class RvDetallesAdapter extends RecyclerView.Adapter<NuevaVentaActivity.RvDetallesVH> {

        @NonNull
        @Override
        public NuevaVentaActivity.RvDetallesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_nueva_venta_item_detalle, parent, false);
            return new NuevaVentaActivity.RvDetallesVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NuevaVentaActivity.RvDetallesVH holder, int position) {
            VentaDetalle detalle = NuevaVentaActivity.INSTANCE.venta.getDetalles().get(position);
            holder.producto.setText(detalle.getProducto().getNombre());
            holder.precioCantidad.setText(String.format("PRECIO: %s Gs CANTIDAD: %s", detalle.getProducto().getPrecio(), detalle.getCantidad()));
            holder.subtotal.setText(String.format("SUBTOTAL: %s Gs", detalle.getSubtotal()));
        }

        @Override
        public int getItemCount() {
            return NuevaVentaActivity.INSTANCE.venta.getDetalles().size();
        }
    }

    public static class RvDetallesVH extends RecyclerView.ViewHolder {
        @BindView(R.id.txtProducto)
        TextView producto;

        @BindView(R.id.txtPrecioCantidad)
        TextView precioCantidad;

        @BindView(R.id.txtSubtotal)
        TextView subtotal;

        public RvDetallesVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
