package es.upm.miw.fem.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Paquete implements Serializable {

    private String id;
    private Long fechaInicio;
    private Long fechaEntrega;
    private String origen;
    private String destino;
    private List<Incidencia> incidencias = new ArrayList<>();

    public Paquete() {
        // Default constructor required for calls to DataSnapshot.getValue(class)
    }

    public Paquete(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Long fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public boolean isEntregado() {
        return fechaEntrega != null;
    }

    private Date fechaInicioAsDate() {
        return new LongToDateConverter().convert(fechaInicio);
    }

    private Date fechaEntregaAsDate() {
        return new LongToDateConverter().convert(fechaEntrega);
    }

    public String fechaInicioAsString() {
        return new DateToStringConverter().convert(fechaInicioAsDate());
    }

    public String fechaEntregaAsString() {
        return new DateToStringConverter().convert(fechaEntregaAsDate());
    }

    public void add(Incidencia incidencia) {
        incidencia.setPaqueteId(this.id);
        incidencias.add(incidencia);
    }

}