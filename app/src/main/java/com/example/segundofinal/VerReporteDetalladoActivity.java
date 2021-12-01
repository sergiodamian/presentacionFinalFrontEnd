package com.example.segundofinal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.segundofinal.pojos.ReporteDetalladoItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerReporteDetalladoActivity extends AppCompatActivity {

    private static volatile VerReporteDetalladoActivity INSTANCE;

    @BindView(R.id.etFiltroProducto)
    EditText etFiltroProducto;

    @BindView(R.id.etFiltroFechaDesde)
    EditText etFiltroFechaDesde;

    @BindView(R.id.etFiltroFechaHasta)
    EditText etFiltroFechaHasta;

    @BindView(R.id.rvItems)
    RecyclerView rvItems;

    @BindView(R.id.btnFiltrar)
    Button btnFiltrar;

    @BindView(R.id.btnLimpiar)
    Button btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_detallado);
        ButterKnife.bind(this);

        VerReporteDetalladoActivity.INSTANCE = this;

        inicializarFiltroFechaDesde();
        inicializarFiltroFechaHasta();
        inicializarListaItems();
        inicializarBtnFiltrar();
        inicializarBtnLimpiar();
    }

    private void inicializarFiltroFechaDesde() {
        etFiltroFechaDesde.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
            int monthOfYear = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);
            String fecha = etFiltroFechaDesde.getText().toString();
            if (fecha != null && !fecha.equals("")) {
                String[] parts = fecha.split(Pattern.quote("/"));
                dayOfMonth = Integer.valueOf(parts[0], 10);
                monthOfYear = Integer.valueOf(parts[1], 10) - 1;
                year = Integer.valueOf(parts[2], 10);
            }

            DatePickerDialog dpd = DatePickerDialog.newInstance(VerReporteDetalladoActivity.INSTANCE::onFechaDesdeDateSet, year, monthOfYear, dayOfMonth);
            dpd.show(VerReporteDetalladoActivity.INSTANCE.getSupportFragmentManager(), "Datepickerdialog");
        });
    }

    private void inicializarFiltroFechaHasta() {
        etFiltroFechaHasta.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
            int monthOfYear = now.get(Calendar.MONTH);
            int year = now.get(Calendar.YEAR);
            String fecha = etFiltroFechaHasta.getText().toString();
            if (fecha != null && !fecha.equals("")) {
                String[] parts = fecha.split(Pattern.quote("/"));
                dayOfMonth = Integer.valueOf(parts[0], 10);
                monthOfYear = Integer.valueOf(parts[1], 10) - 1;
                year = Integer.valueOf(parts[2], 10);
            }

            DatePickerDialog dpd = DatePickerDialog.newInstance(VerReporteDetalladoActivity.INSTANCE::onFechaHastaDateSet, year, monthOfYear, dayOfMonth);
            dpd.show(VerReporteDetalladoActivity.INSTANCE.getSupportFragmentManager(), "Datepickerdialog");
        });
    }

    private void onFechaDesdeDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etFiltroFechaDesde.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
    }

    private void onFechaHastaDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etFiltroFechaHasta.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
    }

    private void inicializarListaItems() {
        VerReporteDetalladoActivity.RvItemsAdapter adapter = new VerReporteDetalladoActivity.RvItemsAdapter(new LinkedList<>());
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);
    }

    private void inicializarBtnFiltrar() {
        btnFiltrar.setOnClickListener(v -> {
            // Obtener filtros.
            String filtroProducto = etFiltroProducto.getText().toString();
            Date filtroFechaDesde = obtenerFecha(etFiltroFechaDesde.getText().toString());
            Date filtroFechaHasta = obtenerFecha(etFiltroFechaHasta.getText().toString());

            // Aplicar filtros sobre ventas realizadas.
            MyApplication app = (MyApplication) getApplication();
            List<ReporteDetalladoItem> items = app.generarReporteDetallado(filtroProducto, filtroFechaDesde, filtroFechaHasta);

            // Actualizar lista de datos.
            ((RvItemsAdapter) rvItems.getAdapter()).items = items;
            ((RvItemsAdapter) rvItems.getAdapter()).notifyDataSetChanged();
        });
    }

    private void inicializarBtnLimpiar() {
        btnLimpiar.setOnClickListener(v -> {
            // Actualizar filtros.
            etFiltroProducto.setText("");
            etFiltroFechaDesde.setText("");
            etFiltroFechaHasta.setText("");

            // Actualizar lista de datos.
            ((RvItemsAdapter) rvItems.getAdapter()).items = new LinkedList<>();
            ((RvItemsAdapter) rvItems.getAdapter()).notifyDataSetChanged();
        });
    }

    private Date obtenerFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return null;
        }

        String[] parts = fecha.split(Pattern.quote("/"));
        int dayOfMonth = Integer.valueOf(parts[0], 10);
        int monthOfYear = Integer.valueOf(parts[1], 10) - 1;
        int year = Integer.valueOf(parts[2], 10);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        now.set(Calendar.MONTH, monthOfYear);
        now.set(Calendar.YEAR, year);
        return now.getTime();
    }

    public static class RvItemsAdapter extends RecyclerView.Adapter<VerReporteDetalladoActivity.RvItemsVH> {

        private List<ReporteDetalladoItem> items;

        RvItemsAdapter(List<ReporteDetalladoItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public VerReporteDetalladoActivity.RvItemsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_detallado, parent, false);
            return new VerReporteDetalladoActivity.RvItemsVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VerReporteDetalladoActivity.RvItemsVH holder, int position) {
            ReporteDetalladoItem item = items.get(position);
            holder.cliente.setText(String.format("CLIENTE: %s", item.getCliente().getNombres()));
            holder.fecha.setText(String.format("FECHA: %s", format(item.getFecha())));
            holder.producto.setText(String.format("PRODUCTO: %s", item.getProducto().getNombre()));
            holder.cantidad.setText(String.format("CANTIDAD: %s", item.getCantidad()));
            holder.subtotal.setText(String.format("SUBTOTAL: %s Gs", item.getTotal()));
        }

        private String format(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(date);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class RvItemsVH extends RecyclerView.ViewHolder {
        @BindView(R.id.txtCliente)
        TextView cliente;

        @BindView(R.id.txtFecha)
        TextView fecha;

        @BindView(R.id.txtProducto)
        TextView producto;

        @BindView(R.id.txtCantidad)
        TextView cantidad;

        @BindView(R.id.txtSubtotal)
        TextView subtotal;

        public RvItemsVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
