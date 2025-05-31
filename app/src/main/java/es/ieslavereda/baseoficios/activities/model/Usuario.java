package es.ieslavereda.baseoficios.activities.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private int oficio_idOficio;

    public Usuario() { }

    public Usuario(int idUsuario, String nombre, String apellidos, int oficio_idOficio) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.oficio_idOficio = oficio_idOficio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getOficio_idOficio() {
        return oficio_idOficio;
    }

    public void setOficio_idOficio(int Oficio_idOficio) {
        this.oficio_idOficio = Oficio_idOficio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario u = (Usuario) o;
        return idUsuario == u.idUsuario;
    }

    @Override
    public int hashCode() {
        return idUsuario;
    }
}