package es.upm.miw.fem.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
class Incidencia implements Serializable {

    private String id;
    private String descripcion;
    private Long fecha = System.currentTimeMillis();
    private String paqueteId;

    public Incidencia() {
        // Default constructor required for calls to DataSnapshot.getValue(class)
    }

    public Incidencia(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Incidencia(String id, String paqueteId, String descripcion) {
        this.id = id;
        this.paqueteId = paqueteId;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaqueteId() {

        return paqueteId;
    }

    public void setPaqueteId(String paqueteId) {
        this.paqueteId = paqueteId;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date fechaAsDate() {
        return new LongToDateConverter().convert(fecha);
    }

    public String fechaAsString() {
        return new DateToStringConverter().convert(fechaAsDate());
    }

}