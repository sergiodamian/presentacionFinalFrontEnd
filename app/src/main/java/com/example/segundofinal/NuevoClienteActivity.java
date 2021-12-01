package com.example.segundofinal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.segundofinal.pojos.Cliente;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NuevoClienteActivity extends AppCompatActivity {

    @BindView(R.id.etRuc)
    EditText etRuc;

    @BindView(R.id.etNombreApellido)
    EditText etNombreApellido;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.btnGuardar)
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_cliente);
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
            Cliente cliente = new Cliente();
            cliente.setRuc(etRuc.getText().toString());
            cliente.setNombres(etNombreApellido.getText().toString());
            cliente.setEmail(etEmail.getText().toString());
            ((MyApplication) getApplication()).guardarCliente(cliente);

            // Ok.
            limpiarInput();
            Toast.makeText(this, "Cliente Guardado", Toast.LENGTH_LONG).show();
        });
    }

    private String validarInput() {
        // Validar ruc.
        String ruc = etRuc.getText().toString();
        if (ruc == null || ruc.equals("")) {
            return "Se debe especificar un ruc";
        }

        // IMPLEMENTAR OTRAS VALIDACIONES.
        ;

        return null;
    }

    private void limpiarInput() {
        etRuc.setText("");
        etNombreApellido.setText("");
        etEmail.setText("");
    }

}
