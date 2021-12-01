package com.example.segundofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.segundofinal.pojos.Producto;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Data;

public class EditarProductoActivity extends AppCompatActivity {

    @BindView(R.id.etCodigo)
    EditText etCodigo;

    @BindView(R.id.etNombre)
    EditText etNombre;

    @BindView(R.id.etPrecio)
    EditText etPrecio;

    @BindView(R.id.etExistencia)
    EditText etExistencia;

    @BindView(R.id.btnGuardar)
    Button btnGuardar;

    private Params params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);
        ButterKnife.bind(this);

        // Obtener parametro.
        this.params = Parcels.unwrap(getIntent().getParcelableExtra("p"));
        if (this.params == null) {
            throw new IllegalStateException("Se esperaban parametros");
        }

        inicializarFormulario();
        inicializarBtnGuardar();
    }

    private void inicializarFormulario() {
        MyApplication app = (MyApplication) getApplication();
        Producto producto = app.buscarProducto(params.codigo);
        etCodigo.setText(producto.getCodigo());
        etNombre.setText(producto.getNombre());
        etPrecio.setText(String.valueOf(producto.getPrecio()));
        etExistencia.setText(String.valueOf(producto.getExistencia()));
    }

    private void inicializarBtnGuardar() {
        btnGuardar.setOnClickListener(v -> {
            // Validar formulario.
            String err = validarInput();
            if (err != null) {
                Toast.makeText(this, err, Toast.LENGTH_LONG).show();
                return;
            }

            // Crear nueva entidad.
            Producto producto = new Producto();
            producto.setCodigo(etCodigo.getText().toString());
            producto.setNombre(etNombre.getText().toString());
            producto.setPrecio(Double.valueOf(etPrecio.getText().toString()));
            producto.setExistencia(Integer.valueOf(etExistencia.getText().toString()));
            ((MyApplication) getApplication()).editarProducto(params.codigo, producto);

            // Actualizar codigo de producto en caso de que haya sido modificado.
            this.params.setCodigo(producto.getCodigo());

            // Ok.
            Toast.makeText(this, "Producto Editado", Toast.LENGTH_LONG).show();
        });
    }

    private String validarInput() {
        // Validar codigo.
        String codigo = etCodigo.getText().toString();
        if (codigo == null || codigo.equals("")) {
            return "Se debe especificar un código";
        }

        // IMPLEMENTAR OTRAS VALIDACIONES.

        return null;
    }

    @Parcel
    @Data
    public static class Params implements Serializable {
        String codigo;
    }

}
