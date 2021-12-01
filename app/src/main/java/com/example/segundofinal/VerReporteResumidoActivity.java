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

import com.example.segundofinal.pojos.ReporteResumidoItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerReporteResumidoActivity extends AppCompatActivity {

    private static volatile VerReporteResumidoActivity INSTANCE;

    @BindView(R.id.etFiltroCliente)
    EditText etFiltroCliente;

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
        setContentView(R.layout.activity_reporte_resumido);
        ButterKnife.bind(this);

        VerReporteResumidoActivity.INSTANCE = this;

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

            DatePickerDialog dpd = DatePickerDialog.newInstance(VerReporteResumidoActivity.INSTANCE::onFechaDesdeDateSet, year, monthOfYear, dayOfMonth);
            dpd.show(VerReporteResumidoActivity.INSTANCE.getSupportFragmentManager(), "Datepickerdialog");
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

            DatePickerDialog dpd = DatePickerDialog.newInstance(VerReporteResumidoActivity.INSTANCE::onFechaHastaDateSet, year, monthOfYear, dayOfMonth);
            dpd.show(VerReporteResumidoActivity.INSTANCE.getSupportFragmentManager(), "Datepickerdialog");
        });
    }

    private void onFechaDesdeDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etFiltroFechaDesde.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
    }

    private void onFechaHastaDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etFiltroFechaHasta.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year));
    }

    private void inicializarListaItems() {
        VerReporteResumidoActivity.RvItemsAdapter adapter = new VerReporteResumidoActivity.RvItemsAdapter(new LinkedList<>());
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);
    }

    private void inicializarBtnFiltrar() {
        btnFiltrar.setOnClickListener(v -> {
            // Obtener filtros.
            String filtroCliente = etFiltroCliente.getText().toString();
            Date filtroFechaDesde = obtenerFecha(etFiltroFechaDesde.getText().toString());
            Date filtroFechaHasta = obtenerFecha(etFiltroFechaHasta.getText().toString());

            // Aplicar filtros sobre ventas realizadas.
            MyApplication app = (MyApplication) getApplication();
            List<ReporteResumidoItem> items = app.generarReporteResumido(filtroCliente,
                    filtroFechaDesde, filtroFechaHasta);

            // Actualizar lista de datos.
            ((RvItemsAdapter) rvItems.getAdapter()).items = items;
            ((RvItemsAdapter) rvItems.getAdapter()).notifyDataSetChanged();
        });
    }

    private void inicializarBtnLimpiar() {
        btnLimpiar.setOnClickListener(v -> {
            // Actualizar filtros.
            etFiltroCliente.setText("");
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

    public static class RvItemsAdapter extends RecyclerView.Adapter<VerReporteResumidoActivity.RvItemsVH> {

        private List<ReporteResumidoItem> items;

        RvItemsAdapter(List<ReporteResumidoItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public VerReporteResumidoActivity.RvItemsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_resumido,
                    parent, false);
            return new VerReporteResumidoActivity.RvItemsVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VerReporteResumidoActivity.RvItemsVH holder, int position) {
            ReporteResumidoItem item = items.get(position);
            holder.cliente.setText(String.format("CLIENTE: %s", item.getCliente().getNombres()));
            holder.fecha.setText(String.format("FECHA: %s", format(item.getFecha())));
            holder.factura.setText(String.format("FACTURA: %s", item.getFactura()));
            holder.total.setText(String.format("TOTAL: %s Gs", item.getTotal()));
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

        @BindView(R.id.txtFactura)
        TextView factura;

        @BindView(R.id.txtTotal)
        TextView total;

        public RvItemsVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
