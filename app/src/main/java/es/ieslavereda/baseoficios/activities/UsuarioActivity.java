package es.ieslavereda.baseoficios.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import es.ieslavereda.baseoficios.base.Parameters;
import es.ieslavereda.baseoficios.base.ImageDownloader;

public class UsuarioActivity extends BaseActivity {

    private Spinner spinnerOficio;
    private EditText editNombre, editApellidos;
    private Button btnGuardar, btnCancelar;
    private ImageView imageOficio;

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
        imageOficio = findViewById(R.id.imageOficio);

        int usuarioId = getIntent().getIntExtra("usuario_id", -1);
        isEdit = usuarioId != -1;
        if (isEdit) {
            btnGuardar.setText("Guardar");
        }else {
            btnGuardar.setText("Crear");
        }

        btnCancelar.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });

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
                return Connector.getConector().getAsList(Oficio.class, "oficios");
            }

            @Override
            public void doInUI(List<Oficio> data) {
                listaOficios = data != null ? data : new ArrayList<>();

                ArrayList<String> descripciones = new ArrayList<>();
                for (Oficio oficio : listaOficios) {
                    descripciones.add(oficio.getDescripcion());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        UsuarioActivity.this,
                        android.R.layout.simple_spinner_item,
                        descripciones
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOficio.setAdapter(adapter);

                if (!listaOficios.isEmpty()) {
                    mostrarImagenOficio(listaOficios.get(0));
                }

                spinnerOficio.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                        if (position >= 0 && position < listaOficios.size()) {
                            mostrarImagenOficio(listaOficios.get(position));
                        }
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                        imageOficio.setImageResource(R.drawable.ic_launcher_background);
                    }
                });

                if (isEdit && usuario != null) {
                    int pos = 0;
                    for (int i = 0; i < listaOficios.size(); i++) {
                        if (listaOficios.get(i).getIdOficio() == usuario.getOficio_idOficio()) {
                            pos = i;
                            break;
                        }
                    }
                    spinnerOficio.setSelection(pos);
                }
            }
        });
    }

    private void mostrarImagenOficio(Oficio oficio) {
        if (oficio != null && oficio.getImage() != null && !oficio.getImage().isEmpty()) {
            String imageUrl = Parameters.URL_IMAGE_BASE + oficio.getImage();
            ImageDownloader.downloadImage(imageUrl, imageOficio);
        } else {
            imageOficio.setImageResource(R.drawable.ic_launcher_background);
        }
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

                    if (listaOficios != null && !listaOficios.isEmpty()) {
                        int pos = 0;
                        for (int i = 0; i < listaOficios.size(); i++) {
                            if (listaOficios.get(i).getIdOficio() == usuario.getOficio_idOficio()) {
                                pos = i;
                                break;
                            }
                        }
                        spinnerOficio.setSelection(pos);
                        mostrarImagenOficio(listaOficios.get(pos));
                    }
                }
            }
        });
    }

    private void crearUsuario(Usuario usuario) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().post(Usuario.class, usuario, "usuarios");
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null) {
                    Toast.makeText(UsuarioActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });
    }

    private void actualizarUsuario(Usuario usuario) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().put(Usuario.class, usuario, "usuarios");
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null) {
                    Toast.makeText(UsuarioActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });
    }
}