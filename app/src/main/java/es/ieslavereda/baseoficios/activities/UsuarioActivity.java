package es.ieslavereda.baseoficios.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.Oficio;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;

public class UsuarioActivity extends BaseActivity {

    private Spinner spinnerOficio;
    private EditText editNombre, editApellidos;
    private Button btnGuardar, btnCancelar;

    private Usuario usuario;
    private boolean isEdit = false;

    private List<Oficio> listaOficios;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_layout);

        spinnerOficio = findViewById(R.id.spinnerOficio);
        editNombre = findViewById(R.id.editNombre);
        editApellidos = findViewById(R.id.editApellidos);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        int usuarioId = getIntent().getIntExtra("usuario_id", -1);
        isEdit = usuarioId != -1;

        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            String apellidos = editApellidos.getText().toString().trim();

            if (nombre.isEmpty() || apellidos.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPosition = spinnerOficio.getSelectedItemPosition();
            if (selectedPosition < 0 || selectedPosition >= listaOficios.size()) {
                Toast.makeText(this, "Selecciona un oficio válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Oficio oficioSeleccionado = listaOficios.get(selectedPosition);

            if (isEdit) {
                usuario = new Usuario(usuario.getIdUsuario(), nombre, apellidos, oficioSeleccionado.getIdOficio());
                actualizarUsuario(usuario);
            } else {
                Usuario nuevo = new Usuario(0, nombre, apellidos, oficioSeleccionado.getIdOficio());
                crearUsuario(nuevo);
            }
        });

        cargarOficios();

        if (isEdit) {
            cargarUsuario(usuarioId);
        }
    }

    private void cargarOficios() {
        executeCall(new CallInterface<List<Oficio>>() {
            @Override
            public List<Oficio> doInBackground() throws Exception {
                return Connector.getConector().getAsList(Oficio.class, "oficios/");
            }

            @Override
            public void doInUI(List<Oficio> data) {
                listaOficios = data;

                ArrayList<String> descripciones = new ArrayList<>();
                for (Oficio oficio : data) {
                    descripciones.add(oficio.getDescripcion());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UsuarioActivity.this,
                        android.R.layout.simple_spinner_item,
                        descripciones
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOficio.setAdapter(adapter);

                if (isEdit && usuario != null) {
                    int pos = 0;
                    for (int i = 0; i < listaOficios.size(); i++) {
                        if (listaOficios.get(i).getIdOficio() == usuario.getOficio_idOficio()) {
                            pos = i;
                        }
                    }
                    spinnerOficio.setSelection(pos);
                }
            }
        });
    }

    private void cargarUsuario(int id) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().get(Usuario.class, "usuarios/" + id);
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null) {
                    usuario = data;
                    editNombre.setText(usuario.getNombre());
                    editApellidos.setText(usuario.getApellidos());

                    if (listaOficios != null) {
                        int pos = 0;
                        for (int i = 0; i < listaOficios.size(); i++) {
                            if (listaOficios.get(i).getIdOficio() == usuario.getOficio_idOficio()) {
                                pos = i;
                            }
                        }
                        spinnerOficio.setSelection(pos);
                    }
                }
            }
        });
    }

    private void crearUsuario(Usuario usuario) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().post(Usuario.class, usuario, "usuarios/");
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null) {
                    Toast.makeText(UsuarioActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void actualizarUsuario(Usuario usuario) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().put(Usuario.class, usuario, "usuarios/");
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null) {
                    Toast.makeText(UsuarioActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
