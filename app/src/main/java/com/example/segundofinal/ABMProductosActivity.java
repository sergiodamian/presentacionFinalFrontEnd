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

import com.example.segundofinal.pojos.Producto;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ABMProductosActivity extends AppCompatActivity {

    private static volatile ABMProductosActivity INSTANCE;

    @BindView(R.id.txtCantidadProductos)
    TextView txtCantidadProductos;

    @BindView(R.id.btnNuevoProducto)
    Button btnNuevoProducto;

    @BindView(R.id.rvProductos)
    RecyclerView rvProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abm_productos);
        ButterKnife.bind(this);

        ABMProductosActivity.INSTANCE = this;

        inicializarTituloProductos();
        inicializarBtnNuevoProducto();
        inicializarListaProductos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        inicializarTituloProductos();
        inicializarListaProductos();
    }

    private void inicializarTituloProductos() {
        MyApplication app = (MyApplication) getApplication();
        int cantidad = app.getProductos().size();
        txtCantidadProductos.setText(String.format("TOTAL DE PRODUCTOS: %d", cantidad));
    }

    private void inicializarBtnNuevoProducto() {
        btnNuevoProducto.setOnClickListener(v -> {
            Intent intent = new Intent(this, NuevoProductoActivity.class);
            startActivity(intent);
        });
    }

    private void inicializarListaProductos() {
        MyApplication app = (MyApplication) getApplication();
        RvProductosAdapter adapter = new RvProductosAdapter();
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(adapter);
    }

    public static class RvProductosAdapter extends RecyclerView.Adapter<RvProductosVH> {

        @NonNull
        @Override
        public RvProductosVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
            return new RvProductosVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvProductosVH holder, int position) {
            MyApplication app = (MyApplication) ABMProductosActivity.INSTANCE.getApplication();
            Producto producto = app.getProductos().get(position);
            holder.codigo.setText(String.format("CODIGO: %s", producto.getCodigo()));
            holder.nombre.setText(String.format("NOMBRE: %s", producto.getNombre()));
            holder.precio.setText(String.format("PRECIO: %s", producto.getPrecio()));
            holder.existencia.setText(String.format("EXISTENCIA: %s", producto.getExistencia()));
            holder.btnEditar.setOnClickListener(v -> onBtnEditarProducto(producto));
            holder.btnEliminar.setOnClickListener(v -> onBtnEliminarProducto(producto));
        }

        private void onBtnEditarProducto(Producto producto) {
            EditarProductoActivity.Params params = new EditarProductoActivity.Params();
            params.setCodigo(producto.getCodigo());

            Bundle bundle = new Bundle();
            bundle.putParcelable("p", Parcels.wrap(params));
            Intent intent = new Intent(ABMProductosActivity.INSTANCE, EditarProductoActivity.class);
            intent.putExtras(bundle);
            ABMProductosActivity.INSTANCE.startActivity(intent);
        }

        private void onBtnEliminarProducto(Producto producto) {
            MyApplication app = (MyApplication) ABMProductosActivity.INSTANCE.getApplication();
            app.eliminarProducto(producto.getCodigo());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            MyApplication app = (MyApplication) ABMProductosActivity.INSTANCE.getApplication();
            return app.getProductos().size();
        }
    }

    public static class RvProductosVH extends RecyclerView.ViewHolder {
        @BindView(R.id.txtProductoCodigo)
        TextView codigo;

        @BindView(R.id.txtProductoNombre)
        TextView nombre;

        @BindView(R.id.txtProductoPrecio)
        TextView precio;

        @BindView(R.id.txtProductoExistencia)
        TextView existencia;

        @BindView(R.id.btnEditar)
        ImageView btnEditar;

        @BindView(R.id.btnEliminar)
        ImageView btnEliminar;

        public RvProductosVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}