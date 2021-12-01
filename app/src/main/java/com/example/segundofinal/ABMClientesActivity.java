package com.example.segundofinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.segundofinal.pojos.Cliente;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ABMClientesActivity extends AppCompatActivity {

    private static volatile ABMClientesActivity INSTANCE;

    @BindView(R.id.txtCantidadClientes)
    TextView txtCantidadClientes;

    @BindView(R.id.btnNuevoCliente)
    Button btnNuevoCliente;

    @BindView(R.id.rvClientes)
    RecyclerView rvClientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abm_clientes);
        ButterKnife.bind(this);

        ABMClientesActivity.INSTANCE = this;

        inicializarTituloClientes();
        inicializarBtnNuevoCliente();
        inicializarListaClientes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizar lista de clientes.
        rvClientes.getAdapter().notifyDataSetChanged();
    }


    private void inicializarTituloClientes() {
        MyApplication app = (MyApplication) getApplication();
        int cantidad = app.getClientes().size();
        txtCantidadClientes.setText(String.format("TOTAL DE Clientes: %d", cantidad));
    }

    private void inicializarBtnNuevoCliente() {
        btnNuevoCliente.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoClienteActivity.class);
            startActivity(intent);
        });
    }

    private void inicializarListaClientes() {
        MyApplication app = (MyApplication) getApplication();
        ABMClientesActivity.RvClientesAdapter adapter =
                new ABMClientesActivity.RvClientesAdapter(app);
        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        rvClientes.setAdapter(adapter);
    }

    public static class RvClientesAdapter extends RecyclerView.Adapter<ABMClientesActivity.RvClientesVH> {
        private MyApplication app;

        public RvClientesAdapter(MyApplication app) {
            this.app = app;
        }

        @NonNull
        @Override
        public ABMClientesActivity.RvClientesVH onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente,
                    parent,
                    false);
            return new ABMClientesActivity.RvClientesVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ABMClientesActivity.RvClientesVH holder,
                                     int position) {
            Cliente cliente = app.getClientes().get(position);
            holder.ruc.setText(String.format("RUC: %s", cliente.getRuc()));
            holder.txtClienteNombreApellido.setText("NOMBRE y APELLIDO: " + cliente.getNombres());
            holder.txtClienteEmail.setText(String.format("EMAIL: %s", cliente.getEmail()));
            holder.btnEditar.setOnClickListener(v -> onBtnEditarCliente(cliente));
            holder.btnEliminar.setOnClickListener(v -> onBtnEliminarCliente(cliente));
        }

        private void onBtnEditarCliente(Cliente cliente) {
            EditarClienteActivity.Params params = new EditarClienteActivity.Params();
            params.setRuc(cliente.getRuc());

            Bundle bundle = new Bundle();
            bundle.putParcelable("p", Parcels.wrap(params));
            Intent intent = new Intent(ABMClientesActivity.INSTANCE, EditarClienteActivity.class);
            intent.putExtras(bundle);
            ABMClientesActivity.INSTANCE.startActivity(intent);
        }

        private void onBtnEliminarCliente(Cliente cliente) {
            app.eliminarCliente(cliente.getRuc());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return app.getClientes().size();
        }
    }

    public static class RvClientesVH extends RecyclerView.ViewHolder {
        @BindView(R.id.txtClienteRuc)
        TextView ruc;

        @BindView(R.id.txtClienteNombreApellido)
        TextView txtClienteNombreApellido;

        @BindView(R.id.txtClienteEmail)
        TextView txtClienteEmail;

        @BindView(R.id.btnEditar)
        ImageView btnEditar;

        @BindView(R.id.btnEliminar)
        ImageView btnEliminar;

        public RvClientesVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
