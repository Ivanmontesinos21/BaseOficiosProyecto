package es.ieslavereda.baseoficios.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.Oficio;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.activities.model.UsuarioAdapter;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;

public class MainActivity extends BaseActivity implements CallInterface<List<Usuario>>, View.OnClickListener {

    private List<Usuario> usuarios = Collections.emptyList();
    private Map<Integer, Oficio> mapOficios = new HashMap<>();

    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private FloatingActionButton fab;

    private Usuario usuarioEliminado;
    private int posicionEliminado;

    private ActivityResultLauncher<Intent> usuarioActivityLauncher;

    private final CallInterface<Usuario> INSERTAR_USUARIO = new CallInterface<Usuario>() {
        @Override
        public Usuario doInBackground() throws Exception {
            return Connector.getConector().post(Usuario.class, usuarioEliminado, "usuarios");
        }

        @Override
        public void doInUI(Usuario data) {

            usuarios.add(posicionEliminado, usuarioEliminado);
            adapter.notifyItemInserted(posicionEliminado);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        fab = findViewById(R.id.floatingActionButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuarioAdapter(usuarios, this, mapOficios);
        recyclerView.setAdapter(adapter);

        usuarioActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        recargarUsuarios();
                    }
                }
        );

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioActivity.class);
            usuarioActivityLauncher.launch(intent);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        int from = viewHolder.getAdapterPosition();
                        int to = target.getAdapterPosition();
                        Collections.swap(usuarios, from, to);
                        adapter.notifyItemMoved(from, to);
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        posicionEliminado = viewHolder.getAdapterPosition();
                        usuarioEliminado = usuarios.get(posicionEliminado);
                        usuarios.remove(posicionEliminado);
                        adapter.notifyItemRemoved(posicionEliminado);
                        eliminarUsuario(usuarioEliminado);
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        showProgress();

        executeCall(new CallInterface<List<Oficio>>() {
            @Override
            public List<Oficio> doInBackground() throws Exception {
                return Connector.getConector().getAsList(Oficio.class, "oficios");
            }

            @Override
            public void doInUI(List<Oficio> data) {
                if (data != null) {
                    mapOficios = new HashMap<>();
                    for (Oficio o : data) {
                        mapOficios.put(o.getIdOficio(), o);
                    }
                    adapter.setOficios(mapOficios);
                    executeCall(MainActivity.this);
                } else {
                    hideProgress();
                }
            }
        });
    }

    @Override
    public List<Usuario> doInBackground() throws Exception {
        return Connector.getConector().getAsList(Usuario.class, "usuarios");
    }

    @Override
    public void doInUI(List<Usuario> data) {
        if (data != null) {
            usuarios = data;
        } else {
            usuarios = Collections.emptyList();
        }
        adapter.setUsuarios(usuarios);
        hideProgress();
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildAdapterPosition(view);
        if (pos != RecyclerView.NO_POSITION && usuarios != null && pos < usuarios.size()) {
            Usuario usuario = usuarios.get(pos);
            Intent intent = new Intent(this, UsuarioActivity.class);
            intent.putExtra("usuario_id", usuario.getIdUsuario());
            usuarioActivityLauncher.launch(intent);
        }
    }

    private void eliminarUsuario(Usuario usuario) {
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().delete(Usuario.class, "usuarios/" + usuario.getIdUsuario());
            }

            @Override
            public void doInUI(Usuario data) {
                Snackbar.make(recyclerView, "Usuario eliminado: " + usuario.getNombre(), Snackbar.LENGTH_LONG)
                        .setAction("Deshacer", v -> executeCall(INSERTAR_USUARIO))
                        .show();
            }
        });
    }

    private void recargarUsuarios() {
        showProgress();
        executeCall(this);
    }
}