package es.ieslavereda.baseoficios.activities.model;

public class Oficio {
    private int idOficio;
    private String descripcion;
    private String image;

    public Oficio() {
    }

    public Oficio(int idOficio, String descripcion, String image) {
        this.idOficio = idOficio;
        this.descripcion = descripcion;
        this.image = image;
    }

    public int getIdOficio() {
        return idOficio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImage() {
        return image;
    }

    public void setIdOficio(int idOficio) {
        this.idOficio = idOficio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
