package es.ieslavereda.baseoficios.activities.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.base.ImageDownloader;
import es.ieslavereda.baseoficios.base.Parameters;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> implements View.OnClickListener {

    private List<Usuario> usuarios;
    private View.OnClickListener listener;
    private Map<Integer, Oficio> mapOficios;

    public UsuarioAdapter(List<Usuario> usuarios, View.OnClickListener listener, Map<Integer, Oficio> mapOficios) {
        this.usuarios = usuarios != null ? usuarios : Collections.emptyList();
        this.listener = listener;
        this.mapOficios = mapOficios != null ? mapOficios : Collections.emptyMap();
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios != null ? usuarios : Collections.emptyList();
        notifyDataSetChanged();
    }

    public void setOficios(Map<Integer, Oficio> mapOficios) {
        this.mapOficios = mapOficios != null ? mapOficios : Collections.emptyMap();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        view.setOnClickListener(this);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);

        holder.textNombreApellido.setText(usuario.getApellidos() + ", " + usuario.getNombre());

        Oficio oficio = mapOficios.get(usuario.getOficio_idOficio());

        if (oficio != null) {
            holder.textOficio.setText(oficio.getDescripcion());
            if (oficio.getImage() != null && !oficio.getImage().isEmpty()) {
                String imageUrl = Parameters.URL_IMAGE_BASE + oficio.getImage();
                ImageDownloader.downloadImage(imageUrl, holder.ImageUsuario);
            } else {
                holder.ImageUsuario.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.textOficio.setText("Oficio desconocido");
            holder.ImageUsuario.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return usuarios != null ? usuarios.size() : 0;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView ImageUsuario;
        TextView textNombreApellido, textOficio;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageUsuario = itemView.findViewById(R.id.imageUsuario);
            textNombreApellido = itemView.findViewById(R.id.textNombreApellido);
            textOficio = itemView.findViewById(R.id.textOficio);
        }
    }
}