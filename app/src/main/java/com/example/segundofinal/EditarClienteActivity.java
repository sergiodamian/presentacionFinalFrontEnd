package com.example.segundofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.segundofinal.pojos.Cliente;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Data;

public class EditarClienteActivity extends AppCompatActivity {

    @BindView(R.id.etRuc)
    EditText etRuc;

    @BindView(R.id.etNombreApellido)
    EditText etNombreApellido;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.btnGuardar)
    Button btnGuardar;

    private Params params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);
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
        Cliente cliente = app.buscarCliente(params.ruc);
        etRuc.setText(cliente.getRuc());
        etNombreApellido.setText(cliente.getNombres());
        etEmail.setText(String.valueOf(cliente.getEmail()));
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
            Cliente cliente = new Cliente();
            cliente.setRuc(etRuc.getText().toString());
            cliente.setNombres(etNombreApellido.getText().toString());
            cliente.setEmail((etEmail.getText().toString()));
            ((MyApplication) getApplication()).editarCliente(params.ruc, cliente);

            // Actualizar codigo de cliente en caso de que haya sido modificado.
            this.params.setRuc(cliente.getRuc());

            // Ok.
            Toast.makeText(this, "Cliente Editado", Toast.LENGTH_LONG).show();
        });
    }

    private String validarInput() {
        // Validar codigo.
        String ruc = etRuc.getText().toString();
        if (ruc == null || ruc.equals("")) {
            return "Se debe especificar un código";
        }

        // IMPLEMENTAR OTRAS VALIDACIONES.
        ;

        return null;
    }

    @Parcel
    @Data
    public static class Params implements Serializable {
        String ruc;
    }

}
