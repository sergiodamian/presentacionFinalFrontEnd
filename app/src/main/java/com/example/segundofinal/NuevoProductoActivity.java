package com.example.segundofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.segundofinal.pojos.Producto;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NuevoProductoActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_producto);
        ButterKnife.bind(this);

        inicializarBtnGuardar();
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
            ((MyApplication) getApplication()).guardarProducto(producto);

            // Volver a primer campo.
            etCodigo.requestFocus();

            // Ok.
            limpiarInput();
            Toast.makeText(this, "Producto Guardado", Toast.LENGTH_LONG).show();
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

    private void limpiarInput() {
        etCodigo.setText("");
        etNombre.setText("");
        etPrecio.setText("");
        etExistencia.setText("");
    }

}
